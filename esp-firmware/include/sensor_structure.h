
#ifndef SENSOR_STRUCTURE_H
#define SENSOR_STRUCTURE_H

#define MEMBER_KEY "member_key"
#define DEVICE_KEY "device_key"
#define WIFI_PASS "wifi_pass"
#define WIFI_SSID "wifi_ssid"
#define PUBLISH_TOPIC "server"

#include <string.h> // Dla funkcji strncpy
#include <stdio.h>
#include "cJSON.h"
#include <stdbool.h>

#define JSON_BUFFER_SIZE 512 // Maksymalny rozmiar bufora JSON
typedef struct WifiNetwork WifiNetwork;
typedef struct Message Message;
typedef enum
{
    DEVICE_CONNECTED,
    SENSOR_VALUE,
    CONFIG,
    UNKNOWN,
} message_type;

struct Message
{
    char member_key[17];
    char device_key[13];
    message_type message_type;

    /* data */
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
Message json_to_message(const char *j);
#endif