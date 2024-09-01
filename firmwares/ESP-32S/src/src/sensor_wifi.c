#include "sensor_wifi.h"
#include "sensor_memory.h"
#include <string.h>
#include <stdio.h>
#include <inttypes.h>
#include "esp_log.h"
#include "esp_event.h"
#include "esp_wifi.h"
#include "esp_netif.h"
#include "nvs_flash.h"
#include "freertos/semphr.h"

static char device_key[13] = {0};
static const char *TAG = "wifi_manager";
static SemaphoreHandle_t wifi_connect_semaphore;
static volatile bool is_connected = false;

void wifi_init(void)
{
    // Inicjalizacja NVS
    esp_err_t ret = nvs_flash_init();
    if (ret == ESP_ERR_NVS_NO_FREE_PAGES || ret == ESP_ERR_NVS_NEW_VERSION_FOUND)
    {
        ESP_ERROR_CHECK(nvs_flash_erase());
        ret = nvs_flash_init();
    }
    ESP_ERROR_CHECK(ret);

    // Tworzenie semafora do synchronizacji
    wifi_connect_semaphore = xSemaphoreCreateBinary();
    if (wifi_connect_semaphore == NULL)
    {
        ESP_LOGE(TAG, "Failed to create WiFi connect semaphore");
        return;
    }
    xSemaphoreGive(wifi_connect_semaphore);

    ESP_LOGI(TAG, "Initializing WiFi");

    ESP_ERROR_CHECK(esp_netif_init());
    ESP_ERROR_CHECK(esp_event_loop_create_default());

    esp_netif_create_default_wifi_sta();
    esp_netif_create_default_wifi_ap();

    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&cfg));

    ESP_ERROR_CHECK(esp_event_handler_register(WIFI_EVENT, ESP_EVENT_ANY_ID, &wifi_event_handler, NULL));
    ESP_ERROR_CHECK(esp_event_handler_register(IP_EVENT, IP_EVENT_STA_GOT_IP, &wifi_event_handler, NULL));

    ConfigEps configEsp;
    esp_err_t retsss = load_config(&configEsp);
    ESP_LOGI(TAG, "WIFI SSID or password  : SSID=%s, Password=%s", configEsp.wifi_ssid, configEsp.wifi_password);
    ESP_LOGI(TAG, "AP SSID or password  : SSID=%s, Password=%s", configEsp.ap_ssid, configEsp.ap_password);
    if (retsss != ESP_OK)
    {
        return;
    }

    wifi_start_sta(configEsp.wifi_ssid, configEsp.wifi_password);
}

void wifi_start_sta(const char *ssid, const char *password)
{
    ESP_LOGI(TAG, "Connecting to WiFi network: SSID=%s", ssid);

    wifi_config_t sta_config = {
        .sta = {
            .ssid = "", 
            .password = "",
            .threshold.authmode = ESP_WIFI_SCAN_AUTH_MODE_THRESHOLD,
        },
    };

    strncpy((char *)sta_config.sta.ssid, ssid, sizeof(sta_config.sta.ssid) - 1);
    strncpy((char *)sta_config.sta.password, password, sizeof(sta_config.sta.password) - 1);

    ESP_LOGI(TAG, "Configuring WiFi...");
    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_APSTA));
    ESP_LOGI(TAG, "Setting station mode...");
    ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_STA, &sta_config));
    ESP_LOGI(TAG, "WiFi configuration completed, starting WiFi...");
    ESP_ERROR_CHECK(esp_wifi_start());
    ESP_LOGI(TAG, "Attempting to connect to WiFi network...");
}

void wifi_disconnect_sta(void)
{
    ESP_LOGI(TAG, "Disconnecting from WiFi network");
    ESP_ERROR_CHECK(esp_wifi_disconnect());
    ESP_ERROR_CHECK(esp_wifi_stop());
}

void wifi_start_ap(const char *ssid, const char *password)
{
    ESP_LOGI(TAG, "Starting AP with SSID: %s", ssid);

    wifi_config_t ap_config = {
        .ap = {
            .ssid = "",
            .ssid_len = 0,
            .password = "",
            .max_connection = 4,
            .authmode = WIFI_AUTH_WPA_WPA2_PSK,
        },
    };


    strncpy((char *)ap_config.ap.ssid, ssid, sizeof(ap_config.ap.ssid) - 1);
    strncpy((char *)ap_config.ap.password, password, sizeof(ap_config.ap.password) - 1);
    ap_config.ap.ssid_len = strlen(ssid);

    if (strlen(password) == 0)
    {
        ap_config.ap.authmode = WIFI_AUTH_OPEN;
    }

    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_APSTA));
    ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_AP, &ap_config));
    ESP_ERROR_CHECK(esp_wifi_start());
}

void wifi_stop_ap(void)
{
    ESP_LOGI(TAG, "Stopping AP");
    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA)); 
}

void wifi_event_handler(void *arg, esp_event_base_t event_base, int32_t event_id, void *event_data)
{
    ESP_LOGI(TAG, "WiFi Event Handler Called");
    ESP_LOGI(TAG, "Event Base: %s, Event ID: %" PRId32, event_base, event_id);

    if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_START)
    {
        ESP_LOGI(TAG, "WiFi STA started, attempting to connect...");
        esp_wifi_connect(); 
    }
    else if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_CONNECTED)
    {
        wifi_stop_ap();
        ESP_LOGI(TAG, "WiFi STA connected");
    }
    else if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_DISCONNECTED)
    {
        ESP_LOGI(TAG, "WiFi STA disconnected");
        ConfigEps configEsp;
        esp_err_t retsss = load_config(&configEsp);
        if (retsss != ESP_OK)
        {
            return;
        }
        wifi_start_ap(configEsp.ap_ssid, configEsp.ap_password);
        esp_wifi_connect();
    }
    else if (event_base == IP_EVENT && event_id == IP_EVENT_STA_GOT_IP)
    {
        ip_event_got_ip_t *event = (ip_event_got_ip_t *)event_data;
        ESP_LOGI(TAG, "Got IP: " IPSTR, IP2STR(&event->ip_info.ip));
        ESP_LOGI(TAG, "Successfully connected to the WiFi network");
    }
}

const char *get_mac_address()
{
    uint8_t mac[6];
    ESP_ERROR_CHECK(esp_base_mac_addr_get(mac));
    sprintf(device_key, "%02X%02X%02X%02X%02X%02X", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
    return device_key;
}
