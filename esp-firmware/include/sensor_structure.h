
#ifndef SENSOR_STRUCTURE_H
#define SENSOR_STRUCTURE_H

#define MEMBER_KEY "member_key"
#define DEVICE_KEY "device_key"
#define WIFI_PASS "wifi_pass"
#define WIFI_SSID "wifi_ssid"
#define PUBLISH_TOPIC "server"
#define MIN(a, b) ((a) < (b) ? (a) : (b))
#define JSON_BUFFER_SIZE 512

#define MAX_S 5

#include <string.h>
#include <stdio.h>
#include <cJSON.h>
#include <stdbool.h>
#include <esp_log.h>

typedef struct WifiNetwork WifiNetwork;
typedef struct OutputTask OutputTask;
typedef struct Message Message;
typedef struct Output Output;
typedef enum
{
    DEVICE_CONNECTED,
    SINGLE_ADC_SIGNAL,
    CONFIG,
    UNKNOWN,
} message_type;

typedef enum
{
    ANALOG,
    DIGITAL,
    UNKNOWN_INPUT
} input_type;

struct Output
{
    input_type type;
    int pin;
    int width;
    int atten;
    int sampling;
    int min_adc;
    int max_adc;
};

struct OutputTask
{
    char member_key[17];
    char device_key[13];
    Output output;
};

struct Message
{
    char member_key[17];
    char device_key[13];
    message_type message_type;
    Output output[MAX_S];
    int outputSensor;
};

struct WifiNetwork
{
    char name[50];
    bool hasPassword;
    int8_t signalStrength;
    WifiNetwork *next; // Wskaźnik na następny element
};

const char *convert_wifi_network_to_json(WifiNetwork *head);
const char *message_type_convert_to_chars(message_type state);
message_type chars_convert_to_message_type(const char *state);
input_type chars_convert_to_input_type(const char *state);
Message json_to_message(const char *j);
#endif