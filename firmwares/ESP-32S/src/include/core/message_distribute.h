#pragma once

#ifndef MESSAGE_DISTRIBUTE_H
#define MESSAGE_DISTRIBUTE_H

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
void distribute(ParsedMessage message);
#endif