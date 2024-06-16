#ifndef SENSOR_RUNNER_H
#define SENSOR_RUNNER_H

#include <esp_log.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <driver/adc.h> //DEPRICATED
#include "sensor_mqtt.h"
#include "sensor_structure.h"

void config_json(Message* message);

void set_pwm(Message *message);

void set_digital(Message *message);

void analog_extort(Message *message);
#endif