
#include "sensor_memory.h"
#include "sensor_mqtt.h"
#include "sensor_wifi.h"
#include "core/message_distribute.h"
#include "payload/message_frame.h"
#include "esp_timer.h"
#include "esp_log.h"
const static char *TAG_MQTT = "MQTT";
const static char *TAG = "TIME";

static esp_mqtt_client_handle_t client = NULL;

static void mqttEventHandler(void *handler_args, esp_event_base_t base, int32_t event_id, void *event_data);

static void mqttEventHandler(void *handler_args, esp_event_base_t base, int32_t event_id, void *event_data)
{

    esp_mqtt_event_handle_t event = (esp_mqtt_event_handle_t)event_data;

    switch ((esp_mqtt_event_id_t)event_id)
    {
    case MQTT_EVENT_CONNECTED:
        ESP_LOGI(TAG_MQTT, "MQTT_EVENT_CONNECTED");
        char uuid_str[37]; // UUIDs are 36 characters plus the null terminator
        generate_uuid(uuid_str);
        publish("88440fe2-f6cd-4b47-8e1b-10367cc48b4d", ";", CONNECTED_ACK);
        esp_mqtt_client_subscribe(client, topicSubscribe(), 2); // Subscribe to the topic
        break;
    case MQTT_EVENT_DISCONNECTED:
        ESP_LOGI(TAG_MQTT, "MQTT_EVENT_DISCONNECTED");
        break;
    case MQTT_EVENT_SUBSCRIBED:
        break;
    case MQTT_EVENT_UNSUBSCRIBED:
        break;
    case MQTT_EVENT_PUBLISHED:
        break;
    case MQTT_EVENT_DATA:
        int64_t start_time = esp_timer_get_time();
        if (strncmp(event->topic, topicSubscribe(), event->topic_len) == 0)
        {
            char *received_data = malloc(event->data_len + 1);
            if (received_data == NULL)
            {
                ESP_LOGE(TAG_MQTT, "Failed to allocate memory for received data");
                return;
            }
            memcpy(received_data, event->data, event->data_len);
            received_data[event->data_len] = '\0';

            MessageFrame new_frame;
            memset(&new_frame, 0, sizeof(MessageFrame));

            int64_t end_time = esp_timer_get_time();       // Pobierz czas końcowy
            int64_t time_taken_us = end_time - start_time; // Oblicz różnicę, czyli czas wykonania funkcji w mikrosekundach
            float time_taken_ms = time_taken_us / 1000.0;  // Przelicz czas na milisekundy
            ESP_LOGI(TAG, "A1: %.3f ms", time_taken_ms);

            chars_to_message_frame(&new_frame, received_data);

            end_time = esp_timer_get_time();        // Pobierz czas końcowy
            time_taken_us = end_time - start_time;  // Oblicz różnicę, czyli czas wykonania funkcji w mikrosekundach
            time_taken_ms = time_taken_us / 1000.0; // Przelicz czas na milisekundy
            ESP_LOGI(TAG, "A2: %.3f ms", time_taken_ms);

            print_message_frame(&new_frame);

            end_time = esp_timer_get_time();        // Pobierz czas końcowy
            time_taken_us = end_time - start_time;  // Oblicz różnicę, czyli czas wykonania funkcji w mikrosekundach
            time_taken_ms = time_taken_us / 1000.0; // Przelicz czas na milisekundy
            ESP_LOGI(TAG, "A3: %.3f ms", time_taken_ms);

            if (new_frame.messageType == PING)
            {
                publish(new_frame.request_id, ";", PING_ACK);
                return;
            }

            ConfigEps config;
            if (load_config(&config) != ESP_OK)
            {
                memset(&config, 0, sizeof(ConfigEps));
                return;
            }
            if (strcmp(new_frame.token, config.token) != 0)
            {
                return;
            }
            if (strcmp(new_frame.member_id, config.member_id) != 0)
            {
                return;
            }
            if (strcmp(new_frame.device_id, get_mac_address()) != 0)
            {
                return;
            }
            if (strcmp(new_frame.firmware, VERSION_FIRMWARE) != 0)
            {
                return;
            }
            end_time = esp_timer_get_time();        // Pobierz czas końcowy
            time_taken_us = end_time - start_time;  // Oblicz różnicę, czyli czas wykonania funkcji w mikrosekundach
            time_taken_ms = time_taken_us / 1000.0; // Przelicz czas na milisekundy
            ESP_LOGI(TAG, "A4: %.3f ms", time_taken_ms);
            ParsedMessage message;
            memset(&message, 0, sizeof(ParsedMessage));
            convert_message_frame_to_internal_object(&message, &new_frame);
            end_time = esp_timer_get_time();        // Pobierz czas końcowy
            time_taken_us = end_time - start_time;  // Oblicz różnicę, czyli czas wykonania funkcji w mikrosekundach
            time_taken_ms = time_taken_us / 1000.0; // Przelicz czas na milisekundy
            ESP_LOGI(TAG, "A5: %.3f ms", time_taken_ms);
            distribute(message);
            free(received_data);
            end_time = esp_timer_get_time();        // Pobierz czas końcowy
            time_taken_us = end_time - start_time;  // Oblicz różnicę, czyli czas wykonania funkcji w mikrosekundach
            time_taken_ms = time_taken_us / 1000.0; // Przelicz czas na milisekundy
            ESP_LOGI(TAG, "A6: %.3f ms", time_taken_ms);
        }
        break;
    case MQTT_EVENT_ERROR:
        break;
    default:
        break;
    }
}

void publish(const char *request_id, const char *payload, MessageType type)
{
    ConfigEps config;
    if (load_config(&config) == ESP_FAIL)
    {
        return;
    }

    MessageFrame frame;
    create_message_frame_by_field(&frame, VERSION_FIRMWARE, get_mac_address(),
                                  config.member_id, type, config.token, request_id, payload);

    char messageFrameToSend[512];
    message_frame_to_chars(&frame, messageFrameToSend);
    print_message_frame(&frame);
    esp_mqtt_client_publish(client, PUBLISH_TOPIC, messageFrameToSend, 0, 2, 0);
}

void mqtt_initial()
{

    ConfigEps config;
    if (load_config(&config) == ESP_FAIL)
    {
        return;
    }

    if (client != NULL)
    {
        esp_mqtt_client_stop(client);
        esp_mqtt_client_destroy(client);
        client = NULL;
    }

    char server_uri[50];

    strcpy(server_uri, "mqtt://");
    strcat(server_uri, config.server_ip);
    strcat(server_uri, ":1883");

    const esp_mqtt_client_config_t mqtt_cfg = {
        .broker.address.uri = server_uri
    };

    client = esp_mqtt_client_init(&mqtt_cfg);
    esp_mqtt_client_register_event(client, ESP_EVENT_ANY_ID, mqttEventHandler, NULL);
    esp_mqtt_client_start(client);
}