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

typedef struct ConfigEps ConfigEps;

typedef struct ParsedMessage ParsedMessage;
typedef struct AnalogReadSetUp AnalogReadSetUp;
typedef struct PwmWriteSetUp PwmWriteSetUp;
typedef struct DigitalSetUp DigitalSetUp;
typedef struct PwmWriteRequest PwmWriteRequest;
typedef struct DigitalWriteRequest DigitalWriteRequest;
typedef struct AnalogReadOneShotRequest AnalogReadOneShotRequest;

struct PwmWriteSetUp
{
    int gpio;
    int frequency;
    int resolution;
    int duty;
};

struct DigitalSetUp
{
    int gpio;
    int mode;
};

struct AnalogReadSetUp
{
    int gpio;
    int resolution;
};

struct PwmWriteRequest
{
    int gpio;
    int duration;
    int duty;
};

struct DigitalWriteRequest
{
    int gpio;
    bool level;
};

struct AnalogReadOneShotRequest
{
    int gpio;
};

struct ParsedMessage
{
    char *request_id;
    MessageType message_type;

    DigitalSetUp  digital_set_up;
    AnalogReadSetUp analog_read_set_up;
    PwmWriteSetUp pwm_write_set_up;

    PwmWriteRequest pwm_write_request;
    DigitalWriteRequest digital_write_request;
    AnalogReadOneShotRequest analog_read_one_shot_request;
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

void generate_uuid(char *uuid_str);
#endif