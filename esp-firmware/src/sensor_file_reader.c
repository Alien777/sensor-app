#include "sensor_file_reader.h"

const static char *TAG_VFS = "SPIFFS";

void file_system_initial()
{

    esp_vfs_spiffs_conf_t conf = {
        .base_path = "/spiffs",
        .partition_label = NULL,
        .max_files = 4,
        .format_if_mount_failed = true};

    esp_err_t ret = esp_vfs_spiffs_register(&conf);

    if (ret != ESP_OK)
    {
        if (ret == ESP_FAIL)
        {
            ESP_LOGE(TAG_VFS, "Failed to mount or format filesystem");
        }
        else if (ret == ESP_ERR_NOT_FOUND)
        {
            ESP_LOGE(TAG_VFS, "Failed to find SPIFFS partition");
        }
        else
        {
            ESP_LOGE(TAG_VFS, "Failed to initialize SPIFFS (%s)", esp_err_to_name(ret));
        }
        return;
    }

    size_t total = 0, used = 0;
    ret = esp_spiffs_info(NULL, &total, &used);
    if (ret != ESP_OK)
    {
        ESP_LOGE(TAG_VFS, "Failed to get SPIFFS partition information (%s)", esp_err_to_name(ret));
    }
    else
    {
        ESP_LOGI(TAG_VFS, "Partition size: total: %d, used: %d", total, used);
    }
}

esp_err_t process_file(const char *filePath, esp_err_t (*process_func)(FILE *, void *), void *arg) {
    FILE *file = fopen(filePath, "r");
    if (file == NULL) {
        ESP_LOGE(TAG_VFS, "Failed to read file");
        return ESP_FAIL;
    }

    esp_err_t result = process_func(file, arg);

    fclose(file);
    return result;
}