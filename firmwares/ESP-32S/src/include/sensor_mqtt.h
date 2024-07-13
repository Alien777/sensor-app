#ifndef SENSOR_MQTT_H
#define SENSOR_MQTT_H

#include <esp_log.h>
#include <mqtt_client.h>
#include "sensor_structure.h"

void mqtt_initial();
void publish(const int config_id, const char *request_id, const char *message, message_type type);

#endif