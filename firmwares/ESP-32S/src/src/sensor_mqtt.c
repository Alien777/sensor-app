
#include "sensor_memory.h"
#include "sensor_mqtt.h"
#include "sensor_wifi.h"
#include "sensor_runner.h"

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
        publish(-1, "{}", DEVICE_CONNECTED);
        esp_mqtt_client_subscribe(client, topicSubscribe(), 0); // Subscribe to the topic
        break;
    case MQTT_EVENT_DISCONNECTED:
        ESP_LOGI(TAG_MQTT, "MQTT_EVENT_DISCONNECTED");
        break;
    case MQTT_EVENT_SUBSCRIBED:
        // Handle subscribed event
        break;
    case MQTT_EVENT_UNSUBSCRIBED:
        // Handle unsubscribed event
        break;
    case MQTT_EVENT_PUBLISHED:
        // Handle published event
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
            config_json(message);
            set_pwm(message);
            set_digital(message);
            analog_extort(message);
            free(message);
        }
        break;
    case MQTT_EVENT_ERROR:
        // Handle error event
        break;
    default:
        break;
    }
}

void publish(const int config_id, const char *message, message_type type)
{
    ConfigEps config;
    if (load_config(&config) == ESP_FAIL)
    {
        return;
    }
    const char *device_key = get_mac_address();
    const char *message_type = message_type_convert_to_chars(type);

    int requiredSize = snprintf(NULL, 0,
                                "{\"%s\":\"%s\",\"%s\":%d,\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"payload\":%s,\"message_type\":\"%s\"}", "token", config.token, "config_identifier", config_id, "version_firmware", VERSION_FIRMWARE,
                                DEVICE_KEY, device_key, MEMBER_KEY, config.member_id, message, message_type) +
                       1;

    char *json = (char *)malloc(requiredSize);

    if (json != NULL)
    {
        snprintf(json, requiredSize,
                 "{\"%s\":\"%s\",\"%s\":%d,\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"payload\":%s,\"message_type\":\"%s\"}", "token", config.token, "config_identifier", config_id, "version_firmware", VERSION_FIRMWARE,
                 DEVICE_KEY, device_key, MEMBER_KEY, config.member_id, message, message_type);
        esp_mqtt_client_publish(client, PUBLISH_TOPIC, json, 0, 2, 0);
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