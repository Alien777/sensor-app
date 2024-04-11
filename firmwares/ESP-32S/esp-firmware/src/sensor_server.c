#include "sensor_server.h"
#include "sensor_wifi.h"
#include "sensor_memory.h"
#include "sensor_file_reader.h"
#include "sensor_structure.h"
#include "sensor_mqtt.h"
static esp_err_t process_file_contents(FILE *file, void *arg);
static esp_err_t fileHandler(httpd_req_t *req);
static int scanHandler(httpd_req_t *req);
static esp_err_t getInformation(httpd_req_t *req);
static esp_err_t saveHandle(httpd_req_t *req);
static void registerUriHandlers();
static void trim(char *str);

const static char *TAG_CONTROLLER = "SERVER";
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
    httpd_resp_sendstr_chunk(req, NULL);
    return ESP_OK;
}

static esp_err_t fileHandler(httpd_req_t *req)
{
    const char *fileName = (const char *)req->user_ctx;

    char filePath[128];
    snprintf(filePath, sizeof(filePath), "/spiffs/%s", fileName);

    const char *type = "text/plain";
    if (strstr(fileName, ".html"))
        type = "text/html";
    else if (strstr(fileName, ".css"))
        type = "text/css";
    else if (strstr(fileName, ".js"))
        type = "application/javascript";
    else if (strstr(fileName, ".ico"))
        type = "image/x-icon";
    httpd_resp_set_type(req, type);

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
    httpd_resp_sendstr(req, response);
    return ESP_OK;
}

static esp_err_t getInformation(httpd_req_t *req)
{
    char response[400];
    char *ssid = currentSSIDConnection();
    char ip_str[16] = {0};
    ConfigEps config;
    load_config(&config);

    esp_netif_t *wifi_netif = esp_netif_get_handle_from_ifkey("WIFI_STA_DEF");
    if (wifi_netif == NULL)
    {
        ESP_LOGE(TAG_CONTROLLER, "Unable to get the network interface handle.");
        return ESP_FAIL;
    }

    esp_netif_ip_info_t ip_info;
    if (esp_netif_get_ip_info(wifi_netif, &ip_info) == ESP_OK)
    {
        esp_ip4addr_ntoa(&ip_info.ip, ip_str, sizeof(ip_str));
    }
    else
    {
        ESP_LOGE(TAG_CONTROLLER, "Unable to get the IP address.");
        strcpy(ip_str, "unknown");
    }

    snprintf(response, sizeof(response), "{\"ssid\":\"%s\", \"ip\":\"%s\", \"device\":\"%s\", \"server_ip\":\"%s\"}", ssid == NULL ? "none" : ssid, ip_str, get_mac_address(), config.server_ip);
    httpd_resp_set_type(req, "application/json");
    httpd_resp_sendstr(req, response);

    return ESP_OK;
}

static esp_err_t saveHandle(httpd_req_t *req)
{
    char content[300];
    int ret, remaining = req->content_len;

    char ssid[33] = {0};
    char password[65] = {0};
    char member[17] = {0};
    char server_ip[17] = {0};
    char token[37] = {0};
    while (remaining > 0)
    {

        if ((ret = httpd_req_recv(req, content, MIN((unsigned int)remaining, sizeof(content)))) <= 0)
        {
            if (ret == HTTPD_SOCK_ERR_TIMEOUT)
            {
                continue;
            }
            return ESP_FAIL;
        }

        char *ssid_start = strstr(content, "name=\"ssid\"\r\n\r\n");
        char *password_start = strstr(content, "name=\"password\"\r\n\r\n");
        char *member_key_start = strstr(content, "name=\"member_key\"\r\n\r\n");
        char *server_ip_start = strstr(content, "name=\"server_ip\"\r\n\r\n");
        char *token_start = strstr(content, "name=\"token\"\r\n\r\n");
        if (ssid_start)
        {
            sscanf(ssid_start + strlen("name=\"ssid\"\r\n\r\n"), "%32[^\r]", ssid);
            trim(ssid);
        }

        if (password_start)
        {
            sscanf(password_start + strlen("name=\"password\"\r\n\r\n"), "%64[^\r]", password);
            trim(password);
        }

        if (member_key_start)
        {
            sscanf(member_key_start + strlen("name=\"member_key\"\r\n\r\n"), "%16[^\r]", member);
            trim(member);
        }

        if (server_ip_start)
        {
            sscanf(server_ip_start + strlen("name=\"server_ip\"\r\n\r\n"), "%16[^\r]", server_ip);
            trim(server_ip);
        }
        if (token_start)
        {
            sscanf(token_start + strlen("name=\"token\"\r\n\r\n"), "%36[^\r]", token);
            trim(token);
        }

        remaining -= ret;
    }

    ESP_LOGI(TAG_CONTROLLER, "ssid %s password %s member %s server %s", ssid, password, member, server_ip);

    if (strlen(ssid) > 0 && strlen(password) > 0)
    {
        save_wifi_credentials(ssid, password);
    }

    if (strlen(member) > 0)
    {
        save_member_key(member);
    }

    if (strlen(server_ip) > 0)
    {
        save_server_ip(server_ip);
    }

    if (strlen(token) > 0)
    {
        save_token(token);
    }

    if (strlen(ssid) > 0 && strlen(password) > 0)
    {
        connect(ssid, password);
    }

    if (strlen(member) > 0 || strlen(server_ip) > 0)
    {
        mqtt_initial();
    }

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
        {"/favicon.ico", HTTP_GET, fileHandler, (void *)"favicon.ico"},
        {"/availables-ssid", HTTP_GET, scanHandler, NULL},
        {"/information", HTTP_GET, getInformation, NULL},
        {"/save", HTTP_POST, saveHandle, NULL}};

    int numHandlers = sizeof(uriHandlers) / sizeof(uriHandlers[0]);

    for (int i = 0; i < numHandlers; ++i)
    {
        httpd_register_uri_handler(server, &uriHandlers[i]);
    }
}

static void trim(char *str)
{
    int len = strlen(str);
    int start = 0, end = len - 1;

    while (start <= end && isspace((unsigned char)str[start]))
    {
        start++;
    }

    while (end >= start && isspace((unsigned char)str[end]))
    {
        end--;
    }

    if (start > end)
    {
        str[0] = '\0';
    }
    else
    {
        int shift = start;
        for (int i = 0; i <= end - start; i++)
        {
            str[i] = str[i + shift];
        }
        str[end - start + 1] = '\0';
    }
}