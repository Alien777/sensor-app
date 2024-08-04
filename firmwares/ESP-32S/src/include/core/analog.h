#pragma once

#ifndef ANALOG_H
#define ANALOG_H

#include <esp_log.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <freertos/timers.h>
#include "esp_adc/adc_oneshot.h"
 
#include "sensor_mqtt.h"
#include "sensor_structure.h"
#include "payload/message_type.h"
void request_for_analog_value(Message *message);
void setup_analog(Message *message);
void reset_analog_settings();

#endif