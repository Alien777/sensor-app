#pragma once

#ifndef SENSOR_CONFIG_H
#define SENSOR_CONFIG_H

#include <esp_log.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <freertos/timers.h>

#include "sensor_mqtt.h"
#include "sensor_structure.h"
#include "core/analog.h"
#include "core/digital.h"
#include "core/pwm.h"
#include "payload/message_type.h"
void setup_config(Message *message);
#endif