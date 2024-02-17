
#ifndef SENSOR_STRUCTURE_H
#define SENSOR_STRUCTURE_H

#define MEMBER_KEY "member_key"
#define DEVICE_KEY "device_key"
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
typedef struct AnalogReaderTask AnalogReaderTask;
typedef struct Message Message;
typedef struct AnalogConfig AnalogConfig;
typedef struct ConfigEps ConfigEps;
typedef enum
{
    DEVICE_CONNECTED,
    SINGLE_ADC_SIGNAL,
    CONFIG,
    UNKNOWN,
} message_type;



struct AnalogConfig
{
    int pin;
    int width;
    int atten;
    int sampling;
    int min_adc;
    int max_adc;
};

struct AnalogReaderTask
{
    char member_key[17];
    char device_key[13];
    int config_id;
    AnalogConfig analog_reader;
};

struct Message
{
    char member_key[17];
    char device_key[13];
    char version[8];
    int config_id;
    message_type message_type;
    AnalogConfig analog_reader[MAX_S];
    int analog_reader_size;
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
    char member_key[17];
    char server_ip[17];
    bool inited;
};

const char *convert_wifi_network_to_json(WifiNetwork *head);
const char *message_type_convert_to_chars(message_type state);
const char *topicSubscribe();
message_type chars_convert_to_message_type(const char *state);
esp_err_t json_to_message(const char *j, Message* msg);
#endif