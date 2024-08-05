#pragma once

#ifndef PWM_H
#define PWM_H

#include <esp_log.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <freertos/timers.h>
#include <driver/ledc.h>

#include "sensor_mqtt.h"
#include "sensor_structure.h"
#include "payload/message_type.h"
void set_pwm(Message *message);
void setup_pwm(Message *message);
void reset_pwm_settings();

#endif