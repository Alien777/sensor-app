
#include "sensor_memory.h"
#include "sensor_mqtt.h"
#include "sensor_wifi.h"
#include "sensor_runner.h"

const static char *TAG_MQTT = "MQTT";
static char topic[100] = {0};
static esp_mqtt_client_handle_t client = NULL;

static void mqttEventHandler(void *handler_args, esp_event_base_t base, int32_t event_id, void *event_data);

static void mqttEventHandler(void *handler_args, esp_event_base_t base, int32_t event_id, void *event_data)
{
    ESP_LOGI(TAG_MQTT, "Event dispatched from event loop base=%s topic=%s, event_id=%" PRIi32 "", base, topic, event_id);
    esp_mqtt_event_handle_t event = (esp_mqtt_event_handle_t)event_data;

    switch ((esp_mqtt_event_id_t)event_id)
    {
    case MQTT_EVENT_CONNECTED:
        ESP_LOGI(TAG_MQTT, "MQTT_EVENT_CONNECTED");
        publish("{}", DEVICE_CONNECTED);
        esp_mqtt_client_subscribe(client, topic, 0); // Subscribe to the topic
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
        if (strncmp(event->topic, topic, event->topic_len) == 0)
        {
            ESP_LOGI(TAG_MQTT, "Received data: %s", event->data);
            Message message = json_to_message(event->data);
            lisening_on_port(message);
        }
        break;
    case MQTT_EVENT_ERROR:
        // Handle error event
        break;
    default:
        break;
    }
}

void publish(const char *message, message_type type)
{
    char member[17] = {0};
    get_member_key(member, sizeof(member));

    const char *device_key = get_mac_address();
    const char *message_type = message_type_convert_to_chars(type);

    // Szacowanie wymaganego rozmiaru
    int requiredSize = snprintf(NULL, 0,
                                "{\"%s\":\"%s\",\"%s\":\"%s\",\"message\":\"%s\",\"message_type\":\"%s\"}",
                                DEVICE_KEY, device_key, MEMBER_KEY, member, message, message_type) +
                       1;

    char *json = (char *)malloc(requiredSize);

    if (json != NULL)
    {
        // Tworzenie JSON
        snprintf(json, requiredSize,
                 "{\"%s\":\"%s\",\"%s\":\"%s\",\"message\":\"%s\",\"message_type\":\"%s\"}",
                 DEVICE_KEY, device_key, MEMBER_KEY, member, message, message_type);

        // Publikowanie wiadomości
        esp_mqtt_client_publish(client, PUBLISH_TOPIC, json, 0, 2, 0);

        // Zwolnienie pamięci
        free(json);
    }
    else
    {
        // Obsługa błędu alokacji pamięci
    }
}

void mqtt_initial()
{
    char member[17] = {0};
    get_member_key(member, sizeof(member));

    const char *deviceKey = get_mac_address(); // Uwaga: zmieniłem get_device_ey na get_device_key

    // Wyczyszczenie bufora topic
    memset(topic, 0, sizeof(topic));

    // Bezpieczne dodanie początkowego "/"
    strncat(topic, "/", sizeof(topic) - 1);

    // Dodanie member do topic, zabezpieczenie przed przepełnieniem
    strncat(topic, member, sizeof(topic) - strlen(topic) - 1);

    // Dodanie deviceKey do topic, zabezpieczenie przed przepełnieniem
    strncat(topic, "/", sizeof(topic) - strlen(topic) - 1);
    strncat(topic, deviceKey, sizeof(topic) - strlen(topic) - 1);

    const esp_mqtt_client_config_t mqtt_cfg = {
        .broker.address.uri = "mqtt://192.168.1.71:1883",
    };
    // starting the process
    client = esp_mqtt_client_init(&mqtt_cfg);
    esp_mqtt_client_register_event(client, ESP_EVENT_ANY_ID, mqttEventHandler, NULL);
    esp_mqtt_client_start(client);
}