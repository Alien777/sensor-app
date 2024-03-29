#ifndef SENSOR_MEMORY_H
#define SENSOR_MEMORY_H
#include <nvs_flash.h>
#include <sensor_structure.h>

void memory_initial(void);
void save_wifi_credentials(const char *ssid, const char *password);
void save_member_key(const char *member_key);
void save_server_ip(const char *server_ip);
esp_err_t load_config(ConfigEps *config);
#endif // SENSOR_MEMORY_H
