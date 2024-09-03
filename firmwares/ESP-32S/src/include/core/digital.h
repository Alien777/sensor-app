#pragma once

#ifndef DIGITAL_H
#define DIGITAL_H

#include <esp_log.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <freertos/timers.h>
#include "driver/gpio.h"

#include "sensor_mqtt.h"
#include "sensor_structure.h"
#include "payload/message_type.h"
void digital_set_up(ParsedMessage  message, DigitalSetUp  payload);
void digital_write_request(ParsedMessage message, DigitalWriteRequest  payload);
#endif