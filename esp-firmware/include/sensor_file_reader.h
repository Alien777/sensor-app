#ifndef SENSOR_FILE_READER_H
#define SENSOR_FILE_READER_H

#include <esp_log.h>
#include <esp_spiffs.h>

void file_system_initial();
esp_err_t process_file(const char *filePath, esp_err_t (*process_func)(FILE *, void *), void *arg);

#endif // SENSOR_MEMORY_H