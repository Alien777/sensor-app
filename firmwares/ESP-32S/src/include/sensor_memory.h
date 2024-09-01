#ifndef SENSOR_MEMORY_H
#define SENSOR_MEMORY_H
#include <nvs_flash.h>
#include <sensor_structure.h>

void memory_initial(void);
void save_wifi_credentials(const char *ssid, const char *password);
void save_ap_credentials(const char *ap_ssid, const char *ap_password);
void save_member_id(const char *member_id);
void save_server_ip(const char *server_ip);
void save_token(const char *token);
esp_err_t load_config(ConfigEps *config);
#endif // SENSOR_MEMORY_H
