#ifndef SENSOR_TIME_H
#define SENSOR_TIME_H
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
#include "esp_system.h"
#include "esp_event.h"
#include "esp_log.h"
#include "nvs_flash.h"
#include "esp_wifi.h"
#include "esp_sntp.h"

void time_initial(void);
void showTime();

#endif
