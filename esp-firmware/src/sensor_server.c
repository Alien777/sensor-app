#include "sensor_server.h"
#include "sensor_wifi.h"
#include "sensor_memory.h"
#include "sensor_file_reader.h"
#include "sensor_structure.h"

 
static esp_err_t process_file_contents(FILE *file, void *arg);
static esp_err_t fileHandler(httpd_req_t *req);
static int scanHandler(httpd_req_t *req);
static esp_err_t getInformation(httpd_req_t *req);
static esp_err_t saveWifiCredentialHandler(httpd_req_t *req);
static esp_err_t saveServerKeyHandler(httpd_req_t *req);
static void registerUriHandlers();

const static char *TAG_CONTROLLER = "CONTROLLER";
static httpd_handle_t server = NULL;

void server_initial()
{
    httpd_config_t config = HTTPD_DEFAULT_CONFIG();
    if (httpd_start(&server, &config) == ESP_OK)
    {
        registerUriHandlers();
    }
}

static esp_err_t process_file_contents(FILE *file, void *arg)
{
    httpd_req_t *req = (httpd_req_t *)arg;
    char lineRead[256];
    while (fgets(lineRead, sizeof(lineRead), file) != NULL)
    {
        httpd_resp_sendstr_chunk(req, lineRead);
    }
    httpd_resp_sendstr_chunk(req, NULL); // End of response
    return ESP_OK;
}

static esp_err_t fileHandler(httpd_req_t *req)
{
    const char *fileName = (const char *)req->user_ctx;

    char filePath[128];
    snprintf(filePath, sizeof(filePath), "/spiffs/%s", fileName);

    // Ustal typ MIME na podstawie rozszerzenia pliku
    const char *type = "text/plain"; // domyślny typ
    if (strstr(fileName, ".html"))
        type = "text/html";
    else if (strstr(fileName, ".css"))
        type = "text/css";
    else if (strstr(fileName, ".js"))
        type = "application/javascript";
    // Możesz dodać więcej typów MIME jeśli potrzebujesz

    httpd_resp_set_type(req, type);

    // Przetwarzanie pliku z przekazaną funkcją pomocniczą
    return process_file(filePath, process_file_contents, req);
}

static int scanHandler(httpd_req_t *req)
{

    WifiNetwork *networksList = scan();

    if (!networksList)
    {
        ESP_LOGE(TAG_CONTROLLER, "Failed to scan networks");
        httpd_resp_send_500(req);
        return ESP_FAIL;
    }

   const char *response = convert_wifi_network_to_json(networksList); // Use your existing function to convert the list to JSON
    if (!response)
    {
        ESP_LOGE(TAG_CONTROLLER, "Failed to convert networks to JSON");
        httpd_resp_send_500(req);
        return ESP_FAIL;
    }
    httpd_resp_set_type(req, "application/json");
    httpd_resp_sendstr(req, response);             // Free the allocated JSON buffer
    return ESP_OK;
}

static esp_err_t getInformation(httpd_req_t *req)
{

    char response[300]; // Increased buffer size for SSID + IP
    char *ssid = currentSSIDConnection();
    char ip_str[16] = {0}; // Buffer for the IP address string

    // Get the network interface handle for the WiFi station interface.
    esp_netif_t *wifi_netif = esp_netif_get_handle_from_ifkey("WIFI_STA_DEF");
    if (wifi_netif == NULL)
    {
        ESP_LOGE(TAG_CONTROLLER, "Unable to get the network interface handle.");
        return ESP_FAIL;
    }

    // Get IP address.
    esp_netif_ip_info_t ip_info;
    if (esp_netif_get_ip_info(wifi_netif, &ip_info) == ESP_OK)
    {
        // Convert the IP address to string
        esp_ip4addr_ntoa(&ip_info.ip, ip_str, sizeof(ip_str));
    }
    else
    {
        ESP_LOGE(TAG_CONTROLLER, "Unable to get the IP address.");
        strcpy(ip_str, "Unavailable"); // Use a placeholder in case of error
    }

    if (ssid == NULL)
    {
        snprintf(response, sizeof(response), "{\"ssid\":null, \"ip\":\"%s\", \"device\":\"%s\"}", ip_str, get_mac_address());
        httpd_resp_set_type(req, "application/json");
        httpd_resp_sendstr(req, response);

        return ESP_OK;
    }

    snprintf(response, sizeof(response), "{\"ssid\":\"%s\", \"ip\":\"%s\", \"device\":\"%s\"}", ssid, ip_str, get_mac_address());
    httpd_resp_set_type(req, "application/json");
    httpd_resp_sendstr(req, response);

    return ESP_OK;
}

static esp_err_t saveWifiCredentialHandler(httpd_req_t *req)
{
    char content[200];
    int ret, remaining = req->content_len;

    char ssid[33] = {0};
    char password[65] = {0};

    while (remaining > 0)
    {
        /* Read the data for the request */
        if ((ret = httpd_req_recv(req, content, MIN((unsigned int)remaining, sizeof(content)))) <= 0)
        {
            if (ret == HTTPD_SOCK_ERR_TIMEOUT)
            {
                continue;
            }
            return ESP_FAIL;
        }

        /* Extract ssid and password from multipart content */
        char *ssid_start = strstr(content, "name=\"ssid\"\r\n\r\n");
        char *password_start = strstr(content, "name=\"password\"\r\n\r\n");

        if (ssid_start)
        {
            sscanf(ssid_start + strlen("name=\"ssid\"\r\n\r\n"), "%32[^\r]", ssid);
        }

        if (password_start)
        {
            sscanf(password_start + strlen("name=\"password\"\r\n\r\n"), "%64[^\r]", password);
        }

        remaining -= ret;
    }
    ESP_LOGE("SS", "ssid %s password %s", ssid, password);
    save_wifi_credentials(ssid, password);
    /* Use ssid and password to connect */
    connect(ssid, password);

    /* Send a response back to the client */
    const char *resp_str = "Trying to connect with provided SSID and password";
    httpd_resp_send(req, resp_str, strlen(resp_str));

    return ESP_OK;
}

static esp_err_t saveServerKeyHandler(httpd_req_t *req)
{
    char content[200];
    int ret, remaining = req->content_len;

    char server_key_read[33] = {0};

    while (remaining > 0)
    {
        /* Read the data for the request */
        if ((ret = httpd_req_recv(req, content, MIN((unsigned int)remaining, sizeof(content)))) <= 0)
        {
            if (ret == HTTPD_SOCK_ERR_TIMEOUT)
            {
                continue;
            }
            return ESP_FAIL;
        }

        /* Extract ssid and password from multipart content */
        char *server_key = strstr(content, "name=\"server_key\"\r\n\r\n");

        if (server_key)
        {
            sscanf(server_key + strlen("name=\"server_key\"\r\n\r\n"), "%32[^\r]", server_key_read);
        }

        remaining -= ret;
    }
    ESP_LOGE("SS", "server key %s ", server_key_read);
    save_member_key(server_key_read);

    const char *resp_str = "Trying to connect with provided SSID and password";
    httpd_resp_send(req, resp_str, strlen(resp_str));

    return ESP_OK;
}



 static void registerUriHandlers()
{
    httpd_uri_t uriHandlers[] = {
        {"/", HTTP_GET, fileHandler, (void *)"index.html"},
        {"/style.css", HTTP_GET, fileHandler, (void *)"style.css"},
        {"/script.js", HTTP_GET, fileHandler, (void *)"script.js"},
        {"/availables-ssid", HTTP_GET, scanHandler, NULL},
        {"/connected-ssid", HTTP_GET, getInformation, NULL},
        {"/connect-wifi", HTTP_POST, saveWifiCredentialHandler, NULL},
        {"/save-server-key", HTTP_POST, saveServerKeyHandler, NULL}};

    int numHandlers = sizeof(uriHandlers) / sizeof(uriHandlers[0]);

    for (int i = 0; i < numHandlers; ++i)
    {
        httpd_register_uri_handler(server, &uriHandlers[i]);
    }
}