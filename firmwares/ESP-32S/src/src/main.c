#include "sensor_wifi.h"
#include "sensor_file_reader.h"
#include "sensor_memory.h"
#include "sensor_mqtt.h"
#include "sensor_server.h"
#include "driver/ledc.h"
#include "esp_err.h"
void start()
{
  file_system_initial();
  memory_initial();
  wifi_initial();
  connection_initial();
  server_initial();
  mqtt_initial();
}

void app_main()
{
  start();
}
