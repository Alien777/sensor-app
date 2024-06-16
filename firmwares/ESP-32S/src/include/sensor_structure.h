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

typedef enum
{
    DEVICE_CONNECTED,
    ANALOG,
    ANALOG_EXTORT,
    PWM,
    CONFIG,
    DIGITAL_WRITE,
    UNKNOWN,
} message_type;

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
    int sampling;
    int min_adc;
    int max_adc;
};

struct PwmTask
{
    char member_key[17];
    char device_key[13];
    int config_id;
    PwmConfig pwm_config;
};

struct AnalogTask
{
    char member_key[17];
    char device_key[13];
    int config_id;
    AnalogConfig analog_config;
};

struct PwmSetup
{
    int pin;
    int duty;
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
    char member_key[17];
    char device_key[13];
    char token[37];
    char version[8];
    int config_id;
    message_type message_type;

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

struct WifiNetwork
{
    char name[50];
    bool hasPassword;
    int8_t signalStrength;
    WifiNetwork *next; // Wskaźnik na następny element
};

struct ConfigEps
{
    char wifi_ssid[32];
    char wifi_password[64];
    char member_id[17];
    char server_ip[17];
    char token[37];
};


const char *convert_wifi_network_to_json(WifiNetwork *head);
const char *message_type_convert_to_chars(message_type state);
const char *topicSubscribe();
message_type chars_convert_to_message_type(const char *state);
esp_err_t json_to_message(const char *j, Message *msg);
#endif