#ifndef SENSOR_RUNNER_H
#define SENSOR_RUNNER_H

#include <esp_log.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <driver/adc.h> //DEPRICATED
#include "sensor_mqtt.h"
#include "sensor_structure.h"

void lisening_output_pin(Message message);
#endif