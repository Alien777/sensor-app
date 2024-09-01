#ifndef SENSOR_STRUCTURE_H
#define SENSOR_STRUCTURE_H

#define MEMBER_KEY "member_id"
#define DEVICE_KEY "device_id"
#define WIFI_PASS "wifi_pass"
#define WIFI_SSID "wifi_ssid"
#define PUBLISH_TOPIC "server"

#define VERSION_FIRMWARE "ESP-32S"

#define MIN(a, b) ((a) < (b) ? (a) : (b))
#define JSON_BUFFER_SIZE 512

#define MAX_S 5

#include <string.h>
#include <stdio.h>
#include <cJSON.h>
#include <stdbool.h>
#include <esp_log.h>
#include <esp_err.h>
#include "payload/message_type.h"
typedef struct WifiNetwork WifiNetwork;
typedef struct AnalogTask AnalogTask;
typedef struct PwmTask PwmTask;
typedef struct Message Message;
typedef struct AnalogConfig AnalogConfig;
typedef struct PwmConfig PwmConfig;
typedef struct DigitalConfig DigitalConfig;
typedef struct ConfigEps ConfigEps;
typedef struct PwmSetup PwmSetup;
typedef struct DigitalSetup DigitalSetup;
typedef struct AnalogReadData AnalogReadData;

struct PwmConfig
{
    int resolution;
    int freq;
    int channel;
    int pin;
};

struct DigitalConfig
{
    int pin;
};

struct AnalogConfig
{
    int pin;
    int width;
    int atten;
};

struct PwmTask
{
    int config_id;
    char request_id[37];
    PwmConfig pwm_config;
};

struct AnalogTask
{
    int config_id;
    char request_id[37];
    AnalogConfig analog_config;
};

struct PwmSetup
{
    int pin;
    int duty;
    int duration;
};

struct DigitalSetup
{
    int pin;
    int value;
};

struct AnalogReadData
{
    int pin;
};

struct Message
{
    int config_id;
    char *request_id;
    MessageType message_type;

    PwmConfig pwm_configs[MAX_S];
    AnalogConfig analog_configs[MAX_S];
    DigitalConfig digital_configs[MAX_S];
    PwmSetup pwn_setup;
    DigitalSetup digital_setup;
    AnalogReadData analog_read_data;

    int analog_configs_size;
    int pwm_configs_size;
    int digital_configs_size;
};

 
struct ConfigEps
{
    char wifi_ssid[32];
    char wifi_password[64];
    char ap_ssid[32];
    char ap_password[64];
    char member_id[17];
    char server_ip[17];
    char token[37];
};

const char *topicSubscribe();
 
#endif