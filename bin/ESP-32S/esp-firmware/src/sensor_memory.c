
#include "sensor_memory.h"
#include "sensor_structure.h"
static void handle_error(esp_err_t err);
static int open_storage(nvs_handle_t *handle, nvs_open_mode mode);
static void close_storage(nvs_handle_t handle);
static esp_err_t save_config(const ConfigEps *config);
static void save_initial();
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

    ConfigEps config;
    esp_err_t err = load_config(&config);
    if (err == ESP_FAIL || config.inited == false)
    {
        ESP_LOGI("MEMORY", "Initialing memory");

        // initiation memory if run first time.
        save_wifi_credentials("none", "none");
        save_member_key("none");
        save_server_ip("none");
        save_initial();
    }
    else
    {
        ESP_LOGI("MEMORY", "Was initialized");
    }
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

void save_member_key(const char *member_key)
{
    ConfigEps config;

    if (load_config(&config) != ESP_OK)
    {
        memset(&config, 0, sizeof(ConfigEps));
    }

    strncpy(config.member_key, member_key, sizeof(config.member_key));

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

static void save_initial()
{
    ConfigEps config;

    if (load_config(&config) != ESP_OK)
    {
        memset(&config, 0, sizeof(ConfigEps));
    }

    config.inited = true;
    esp_err_t err = save_config(&config);
    if (err != ESP_OK)
    {
        handle_error(err);
        return;
    }

    ESP_LOGI("MEMORY", "Save sinitied");
}
