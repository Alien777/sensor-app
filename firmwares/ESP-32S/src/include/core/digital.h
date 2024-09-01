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
void set_digital(Message *message);
void setup_digital(Message *message);
void reset_digital_settings();

#endif