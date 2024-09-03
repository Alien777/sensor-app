#pragma once

#ifndef ANALOG_H
#define ANALOG_H

#include <esp_log.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <freertos/timers.h>
#include <esp_adc/adc_oneshot.h>
#include <esp_adc/adc_cali.h>
#include <esp_adc/adc_cali_scheme.h>
 #include "driver/gpio.h"
#include "sensor_mqtt.h"
#include "sensor_structure.h"
#include "payload/message_type.h"
void analog_read_one_shot_request(ParsedMessage message, AnalogReadOneShotRequest  paylaod);
void analog_read_set_up(ParsedMessage message, AnalogReadSetUp  paylaod);

#endif