
#include "sensor_wifi.h"
#include "sensor_memory.h"

static void event_handler(void *arg, esp_event_base_t event_base, int32_t event_id, void *event_data);
static void connect_task(void *pvParameters);

static void enableAccessPoint();
static void disableAaccessPoint();

const static char *TAG_WIF = "WIFI";
static bool ap_enabled = false;
static bool is_connected = false;
static TaskHandle_t connectionHandle = NULL;
static SemaphoreHandle_t mutex = NULL;
static char device_key[13] = {0};

void wifi_initial()
{
    mutex = xSemaphoreCreateMutex();
    esp_netif_init();
    ESP_ERROR_CHECK(esp_event_loop_create_default());
    esp_netif_create_default_wifi_sta();
    esp_netif_create_default_wifi_ap();
    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&cfg));

    esp_event_handler_instance_t instance_any_id;
    esp_event_handler_instance_t instance_got_ip;
    ESP_ERROR_CHECK(esp_event_handler_instance_register(WIFI_EVENT,
                                                        ESP_EVENT_ANY_ID,
                                                        &event_handler,
                                                        NULL,
                                                        &instance_any_id));
    ESP_ERROR_CHECK(esp_event_handler_instance_register(IP_EVENT,
                                                        IP_EVENT_STA_GOT_IP,
                                                        &event_handler,
                                                        NULL,
                                                        &instance_got_ip));

    wifi_config_t wifi_config = {};
    strncpy((char *)wifi_config.sta.ssid, "test", 32);     // Rzutowanie w stylu C
    strncpy((char *)wifi_config.sta.password, "test", 64); // Rzutowanie w stylu C
    wifi_config.sta.threshold.authmode = ESP_WIFI_SCAN_AUTH_MODE_THRESHOLD;
    wifi_config.sta.sae_pwe_h2e = WPA3_SAE_PWE_BOTH;

    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
    ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_STA, &wifi_config));
    ESP_ERROR_CHECK(esp_wifi_start());

    xTaskCreate(&connect_task, "connect_task", 2048, NULL, 5, &connectionHandle);
    ESP_LOGI(TAG_WIF, "wifi_init_sta zakończony.");
}

static void event_handler(void *arg, esp_event_base_t event_base, int32_t event_id, void *event_data)
{

    if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_DISCONNECTED)
    {
        is_connected = false;
    }
    else if (event_base == IP_EVENT && event_id == IP_EVENT_STA_GOT_IP)
    {
        is_connected = true;
    }
}

void connect(const char *ssid, const char *password)
{
    if (strlen(ssid) >= 32 || strlen(password) >= 64)
    {
        ESP_LOGW(TAG_WIF, "SSID or password too long");
        return;
    }
    ESP_LOGI(TAG_WIF, "Connecting to SSID: %s, Password: %s", ssid, password);
    wifi_config_t wifi_config = {};
    strncpy((char *)wifi_config.sta.ssid, ssid, 32);
    strncpy((char *)wifi_config.sta.password, password, 64);
    wifi_config.sta.threshold.authmode = ESP_WIFI_SCAN_AUTH_MODE_THRESHOLD;
    wifi_config.sta.sae_pwe_h2e = WPA3_SAE_PWE_BOTH;
    if (xSemaphoreTake(mutex, portMAX_DELAY))
    {
        ESP_ERROR_CHECK(esp_wifi_disconnect());
        ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
        ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_STA, &wifi_config));
        ESP_ERROR_CHECK(esp_wifi_connect());
        xSemaphoreGive(mutex);
    }
}

static void enableAccessPoint()
{
    if (ap_enabled)
    {
        ESP_LOGW(TAG_WIF, "AP is already ENABLED");
    }
    else
    {
        ESP_LOGI(TAG_WIF, "Enabling AP");
        esp_wifi_stop();

        wifi_config_t ap_config = {};
        strncpy((char *)ap_config.ap.ssid, WIFI_AP_SSID, 32);         // Rzutowanie w stylu C
        strncpy((char *)ap_config.ap.password, WIFI_AP_PASSWORD, 64); // Rzutowanie w stylu C
        ap_config.ap.max_connection = 4;
        ap_config.ap.authmode = WIFI_AUTH_WPA_WPA2_PSK;

        ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_APSTA));
        ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_AP, &ap_config));
        ESP_ERROR_CHECK(esp_wifi_start());
        ap_enabled = true;
    }
}

static void disableAaccessPoint()
{

    if (!ap_enabled)
    {
        ESP_LOGI(TAG_WIF, "AP is already disabled");
    }
    else
    {
        ap_enabled = false;
        ESP_ERROR_CHECK(esp_wifi_stop());
        ESP_ERROR_CHECK(esp_wifi_start());
        ESP_ERROR_CHECK(esp_wifi_connect());
    }
}

char *currentSSIDConnection()
{
    static wifi_config_t config; // Statyczna, więc jest zerowana na początku i trwa przez cały czas trwania aplikacji
    wifi_ap_record_t ap_info;
    if (esp_wifi_sta_get_ap_info(&ap_info) != ESP_OK)
    {
        return NULL;
    }
    if (esp_wifi_get_config(WIFI_IF_STA, &config) != ESP_OK)
    {
        return NULL;
    }

    return (char *)config.sta.ssid;
}

WifiNetwork *scan()
{
    static WifiNetwork networks[MAX_NETWORKS];
    WifiNetwork *head = NULL;
    WifiNetwork **current = &head;

    wifi_scan_config_t scanConfig = {
        .ssid = NULL,
        .bssid = NULL,
        .channel = 0,
        .show_hidden = false,
        .scan_type = WIFI_SCAN_TYPE_ACTIVE,
        .scan_time = {
            .active = {.min = 50, .max = 60},
        },
    };

    esp_err_t ret = esp_wifi_scan_start(&scanConfig, true);
    if (ret != ESP_OK)
    {
        ESP_LOGE(TAG_WIF, "esp_wifi_scan_start() failed with error: %s", esp_err_to_name(ret));
    }

    uint16_t number = 0;
    ESP_ERROR_CHECK(esp_wifi_scan_get_ap_num(&number));

    if (number > MAX_NETWORKS)
    {
        number = MAX_NETWORKS;
    }

    wifi_ap_record_t apList[MAX_NETWORKS];
    ESP_ERROR_CHECK(esp_wifi_scan_get_ap_records(&number, apList));

    for (int i = 0; i < number; i++)
    {
        WifiNetwork *network = &networks[i];
        strncpy(network->name, (char *)apList[i].ssid, sizeof(network->name));
        network->name[sizeof(network->name) - 1] = '\0';
        network->hasPassword = apList[i].authmode != WIFI_AUTH_OPEN;
        network->signalStrength = apList[i].rssi;
        network->next = (i < (number - 1)) ? &networks[i + 1] : NULL;

        *current = network;
        current = &(network->next);
    }

    return head;
}

static void connect_task(void *pvParameters)
{
    while (1)
    {
        ESP_LOGI("WIFI", "Checking WiFi status...");

        const TickType_t xDelay = pdMS_TO_TICKS(10000);

        vTaskDelay(xDelay);
        ESP_LOGI("WIFI", "Attempting to acquire WiFi mutex.");

        if (xSemaphoreTake(mutex, portMAX_DELAY))
        {

            ESP_LOGI("WIFI", "Check connection");
            if (is_connected == false)
            {

                ESP_LOGW("WIFI", "Failed to initiate WiFi connection (ENABLED AP): ");
                enableAccessPoint();
                connection_initial();
            }
            else
            {
                ESP_LOGW("WIFI", "Connected to initiate WiFi connection (DISABLED AP):");
                disableAaccessPoint();
            }

            xSemaphoreGive(mutex);
        }
        else
        {
            ESP_LOGE("WIFI", "Failed to acquire WiFi mutex.");
        }
    }
}

void connection_initial()
{

    ConfigEps configEsp;
    load_config(&configEsp);
    esp_err_t ret = load_config(&configEsp);
    ESP_LOGW("MAIN", "ssid or password is empty %s %s ", configEsp.wifi_ssid, configEsp.wifi_password);
    if (ret == ESP_OK)
    {

        if (strlen(configEsp.wifi_ssid) > 0 && strlen(configEsp.wifi_password) > 0)
        {
            ESP_LOGE("MAIN", "ssid or password is empty %s %s ", configEsp.wifi_ssid, configEsp.wifi_password);
            connect(configEsp.wifi_ssid, configEsp.wifi_password);
        }
        else
        {
            ESP_LOGE("MAIN", "ssid or password is empty");
        }
    }
    else
    {
        ESP_LOGE("MAIN", "error to get ssid and password");
    }
}

const char *get_mac_address()
{
    uint8_t mac[6];
    esp_base_mac_addr_get(mac);
    sprintf(device_key, "%02X%02X%02X%02X%02X%02X", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
    return device_key;
}