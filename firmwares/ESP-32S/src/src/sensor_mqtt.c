
#include "sensor_memory.h"
#include "sensor_mqtt.h"
#include "sensor_wifi.h"
#include "core/sensor_config.h"

const static char *TAG_MQTT = "MQTT";

static esp_mqtt_client_handle_t client = NULL;

static void mqttEventHandler(void *handler_args, esp_event_base_t base, int32_t event_id, void *event_data);

static void mqttEventHandler(void *handler_args, esp_event_base_t base, int32_t event_id, void *event_data)
{
    // ESP_LOGI(TAG_MQTT, "Event dispatched from event loop base=%s topic=%s, event_id=%" PRIi32 "", base, topicSubscribe(), event_id);
    esp_mqtt_event_handle_t event = (esp_mqtt_event_handle_t)event_data;

    switch ((esp_mqtt_event_id_t)event_id)
    {
    case MQTT_EVENT_CONNECTED:
        ESP_LOGI(TAG_MQTT, "MQTT_EVENT_CONNECTED");
        publish(-1, "DEVICE_CONNECTION", "{}", DEVICE_CONNECTED);
        esp_mqtt_client_subscribe(client, topicSubscribe(), 0); // Subscribe to the topic
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
        if (strncmp(event->topic, topicSubscribe(), event->topic_len) == 0)
        {
            ESP_LOGI(TAG_MQTT, "Received data: %s length %d", event->data, event->data_len);
            Message *message = (Message *)malloc(sizeof(Message));
            ESP_LOGI(TAG_MQTT, "Alocated message");
            if (message == NULL)
            {
                ESP_LOGE(TAG_MQTT, "Failed to allocate memory for message");
                return;
            }
            esp_err_t err = json_to_message(event->data, message);
            if (err == ESP_FAIL)
            {
                free(message);
                ESP_LOGE(TAG_MQTT, "Problem with convert message");
                break;
            }
            if (message->message_type == PING)
            {
                publish(-1, message->request_id, "{}", PING_ACK);
                free(message);
                return;
            }
            setup_config(message);
            set_pwm(message);
            set_digital(message);
            request_for_analog_value(message);
            free(message);
        }
        break;
    case MQTT_EVENT_ERROR:
        break;
    default:
        break;
    }
}

void publish(const int config_id, const char *request_id, const char *message, message_type type)
{
    ConfigEps config;
    if (load_config(&config) == ESP_FAIL)
    {
        return;
    }
    const char *device_key = get_mac_address();
    const char *message_type = message_type_convert_to_chars(type);

    int requiredSize = snprintf(NULL, 0,
                                "{\"%s\":\"%s\",\"%s\":%d,\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"payload\":%s,\"message_type\":\"%s\",\"request_id\":\"%s\"}",
                                "token", config.token, "config_identifier", config_id, "version_firmware", VERSION_FIRMWARE,
                                DEVICE_KEY, device_key, MEMBER_KEY, config.member_id, message, message_type, request_id) +
                       1;

    char *json = (char *)malloc(requiredSize);

    if (json != NULL)
    {
        snprintf(json, requiredSize,
                 "{\"%s\":\"%s\",\"%s\":%d,\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"payload\":%s,\"message_type\":\"%s\",\"request_id\":\"%s\"}",
                 "token", config.token, "config_identifier", config_id, "version_firmware", VERSION_FIRMWARE,
                 DEVICE_KEY, device_key, MEMBER_KEY, config.member_id, message, message_type, request_id);


        esp_mqtt_client_publish(client, PUBLISH_TOPIC, json, 0, 1, 0);
        free(json);
    }
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
        .broker.address.uri = server_uri,
    };

    client = esp_mqtt_client_init(&mqtt_cfg);
    esp_mqtt_client_register_event(client, ESP_EVENT_ANY_ID, mqttEventHandler, NULL);
    esp_mqtt_client_start(client);
}