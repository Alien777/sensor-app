#ifndef SENSOR_MEMORY_H
#define SENSOR_MEMORY_H
#include <nvs_flash.h>

void memory_initial(void);
void save_wifi_credentials(const char *ssid, const char *password);
esp_err_t get_wifi_credentials(char *ssid, size_t ssid_size, char *password, size_t password_size);
void save_member_key(const char *member_key);
esp_err_t get_member_key(char *member_key, size_t member_key_size);

#endif // SENSOR_MEMORY_H
