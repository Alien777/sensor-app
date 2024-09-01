
#include "sensor_memory.h"
#include "sensor_structure.h"
static void handle_error(esp_err_t err);
static int open_storage(nvs_handle_t *handle, nvs_open_mode mode);
static void close_storage(nvs_handle_t handle);
static esp_err_t save_config(const ConfigEps *config);
static esp_err_t load_str_and_clear(char *value, const char *key);

static void handle_error(esp_err_t err)
{
    ESP_LOGE("MEMORY", "Occured problem");
}

static int open_storage(nvs_handle_t *handle, nvs_open_mode mode)
{
    return nvs_open("storage", mode, handle);
}

static void close_storage(nvs_handle_t handle)
{
    nvs_close(handle);
}

static esp_err_t save_config(const ConfigEps *config)
{
    nvs_handle_t my_handle;
    esp_err_t err;

    err = open_storage(&my_handle, NVS_READWRITE);
    if (err != ESP_OK)
    {
        return err;
    }

    err = nvs_set_blob(my_handle, "config", config, sizeof(ConfigEps));
    if (err != ESP_OK)
    {
        close_storage(my_handle);
        return err;
    }

    err = nvs_commit(my_handle);
    close_storage(my_handle);
    return err;
}

void memory_initial()
{
    esp_err_t ret = nvs_flash_init();
    if (ret == ESP_ERR_NVS_NO_FREE_PAGES || ret == ESP_ERR_NVS_NEW_VERSION_FOUND)
    {
        ESP_ERROR_CHECK(nvs_flash_erase());
        ret = nvs_flash_init();
    }

    ESP_ERROR_CHECK(ret);

    char wifi_ssid[32];
    char wifi_password[64];
    char ap_ssid[32];
    char ap_password[64];
    char member_id[17];
    char server_ip[17];
    char token[37];

    if (load_str_and_clear(wifi_ssid, "wifi_ssid") == ESP_OK &&
        load_str_and_clear(wifi_password, "wifi_password") == ESP_OK)
    {
        ESP_LOGI("MEMORY", "Init default wifi: %s, %s", wifi_ssid, wifi_password);
        save_wifi_credentials(wifi_ssid, wifi_password);
    }

    if (load_str_and_clear(ap_ssid, "ap_ssid") == ESP_OK &&
        load_str_and_clear(ap_password, "ap_password") == ESP_OK)
    {
        ESP_LOGI("MEMORY", "Init default ap: %s, %s", ap_ssid, ap_password);
        save_ap_credentials(ap_ssid, ap_password);
    }

    if (load_str_and_clear(member_id, "member_id") == ESP_OK)
    {
        ESP_LOGI("MEMORY", "Init default member id: %s", member_id);
        save_member_id(member_id);
    }

    if (load_str_and_clear(server_ip, "server_ip") == ESP_OK)
    {
        ESP_LOGI("MEMORY", "Init default server ip: %s", server_ip);
        save_server_ip(server_ip);
    }

    if (load_str_and_clear(token, "token") == ESP_OK)
    {
        ESP_LOGI("MEMORY", "Init default token: %s", token);
        save_token(token);
    }
}

static esp_err_t load_str_and_clear(char *value, const char *key)
{
    nvs_handle_t my_handle;
    esp_err_t err;

    err = open_storage(&my_handle, NVS_READWRITE);
    if (err != ESP_OK)
    {
        return err;
    }

    size_t required_size;
    err = nvs_get_str(my_handle, key, NULL, &required_size);
    if (err == ESP_OK)
    {
        err = nvs_get_str(my_handle, key, value, &required_size);
        if (err == ESP_OK)
        {
            nvs_erase_key(my_handle, key);
        }
    }

    close_storage(my_handle);
    return err;
}

esp_err_t load_config(ConfigEps *config)
{
    nvs_handle_t my_handle;
    esp_err_t err;

    err = open_storage(&my_handle, NVS_READONLY);
    if (err != ESP_OK)
    {
        return err;
    }

    size_t config_size = sizeof(ConfigEps);
    err = nvs_get_blob(my_handle, "config", config, &config_size);
    close_storage(my_handle);

    return err;
}

void save_wifi_credentials(const char *ssid, const char *password)
{
    ConfigEps config;

    if (load_config(&config) != ESP_OK)
    {
        memset(&config, 0, sizeof(ConfigEps));
    }

    strncpy(config.wifi_ssid, ssid, sizeof(config.wifi_ssid));
    strncpy(config.wifi_password, password, sizeof(config.wifi_password));

    esp_err_t err = save_config(&config);
    if (err != ESP_OK)
    {
        handle_error(err);
        return;
    }

    ESP_LOGI("MEMORY", "Save credential");
}

void save_ap_credentials(const char *ap_ssid, const char *ap_password)
{
    ConfigEps config;

    if (load_config(&config) != ESP_OK)
    {
        memset(&config, 0, sizeof(ConfigEps));
    }

    strncpy(config.ap_ssid, ap_ssid, sizeof(config.ap_ssid));
    strncpy(config.ap_password, ap_password, sizeof(config.ap_password));

    esp_err_t err = save_config(&config);
    if (err != ESP_OK)
    {
        handle_error(err);
        return;
    }

    ESP_LOGI("MEMORY", "Save credential");
}

void save_member_id(const char *member_id)
{
    ConfigEps config;

    if (load_config(&config) != ESP_OK)
    {
        memset(&config, 0, sizeof(ConfigEps));
    }

    strncpy(config.member_id, member_id, sizeof(config.member_id));

    esp_err_t err = save_config(&config);
    if (err != ESP_OK)
    {
        handle_error(err);
        return;
    }

    ESP_LOGI("MEMORY", "Save member key");
}

void save_server_ip(const char *server_ip)
{
    ConfigEps config;

    if (load_config(&config) != ESP_OK)
    {
        memset(&config, 0, sizeof(ConfigEps));
    }

    strncpy(config.server_ip, server_ip, sizeof(config.server_ip));

    esp_err_t err = save_config(&config);
    if (err != ESP_OK)
    {
        handle_error(err);
        return;
    }

    ESP_LOGI("MEMORY", "Save server ip");
}

void save_token(const char *token)
{
    ConfigEps config;

    if (load_config(&config) != ESP_OK)
    {
        memset(&config, 0, sizeof(ConfigEps));
    }

    strncpy(config.token, token, sizeof(config.token));

    esp_err_t err = save_config(&config);
    if (err != ESP_OK)
    {
        handle_error(err);
        return;
    }

    ESP_LOGI("MEMORY", "Save token");
}