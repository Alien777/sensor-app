
#include "sensor_memory.h"
#include "sensor_structure.h"
static void handle_error(esp_err_t err);
static int open_storage(nvs_handle_t *handle, nvs_open_mode mode);
static void close_storage(nvs_handle_t handle);
static esp_err_t nvs_write_str(nvs_handle_t handle, const char *key, const char *value);
static esp_err_t nvs_read_str(nvs_handle_t handle, const char *key, char *value, size_t *length);


static void handle_error(esp_err_t err)
{
}

static int open_storage(nvs_handle_t *handle, nvs_open_mode mode)
{
    return nvs_open("storage", mode, handle);
}

static void close_storage(nvs_handle_t handle)
{
    nvs_close(handle);
}

static esp_err_t nvs_write_str(nvs_handle_t handle, const char *key, const char *value)
{
    esp_err_t err = nvs_set_str(handle, key, value);
    if (err != ESP_OK)
    {
        return err;
    }
    return nvs_commit(handle);
}

static esp_err_t nvs_read_str(nvs_handle_t handle, const char *key, char *value, size_t *length)
{
    return nvs_get_str(handle, key, value, length);
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
}

void save_wifi_credentials(const char *ssid, const char *password)
{
    nvs_handle_t my_handle;
    esp_err_t err;

    err = open_storage(&my_handle, NVS_READWRITE);
    handle_error(err);

    err = nvs_write_str(my_handle, WIFI_SSID, ssid);
    handle_error(err);

    err = nvs_write_str(my_handle, WIFI_PASS, password);
    handle_error(err);

    close_storage(my_handle);
}

esp_err_t get_wifi_credentials(char *ssid, size_t ssid_size, char *password, size_t password_size)
{
    nvs_handle_t my_handle;
    esp_err_t err;

    err = open_storage(&my_handle, NVS_READONLY);
    if (err != ESP_OK)
    {
        return err;
    }

    err = nvs_read_str(my_handle, WIFI_SSID, ssid, &ssid_size);
    if (err != ESP_OK)
    {
        close_storage(my_handle);
        return err;
    }

    err = nvs_read_str(my_handle, WIFI_PASS, password, &password_size);
    if (err != ESP_OK)
    {
        close_storage(my_handle);
        return err;
    }

    close_storage(my_handle);
    return ESP_OK;
}

void save_member_key(const char *member_key)
{
    nvs_handle_t my_handle;
    esp_err_t err;

    err = open_storage(&my_handle, NVS_READWRITE);
    handle_error(err);

    err = nvs_write_str(my_handle, MEMBER_KEY, member_key);
    handle_error(err);

    close_storage(my_handle);
}

esp_err_t get_member_key(char *member_key, size_t member_key_size)
{
    nvs_handle_t my_handle;
    esp_err_t err;

    err = open_storage(&my_handle, NVS_READONLY);
    if (err != ESP_OK)
    {
        return err;
    }

    err = nvs_read_str(my_handle, MEMBER_KEY, member_key, &member_key_size);
    if (err != ESP_OK)
    {
        close_storage(my_handle);
        return err;
    }

    close_storage(my_handle);
    return ESP_OK;
}