#include "sensor_runner.h"

#define MAX_TASKS 10

static void readAnalog(void *pvParameters);
static AnalogReaderTask *deepCopyMessageToOutputTask(const Message *src, int outputIndex);
static void freeOutputTask(AnalogReaderTask *task);
static void closeAllTasks();
static void addTask(TaskHandle_t task, AnalogReaderTask *outputTask);

TaskHandle_t tasks[MAX_TASKS];
AnalogReaderTask *outputDataTask[MAX_TASKS];
int taskCount = 0;

static void readAnalog(void *pvParameters)
{
    AnalogReaderTask *outputTask = (AnalogReaderTask *)pvParameters;

    AnalogConfig *output = &(outputTask->analog_reader);

    adc1_config_channel_atten(output->pin, output->atten);
    adc1_config_width(output->width);

    while (1)
    {
        if (output->sampling > 100)
        {
            vTaskDelay(pdMS_TO_TICKS(output->sampling));
        }
        else
        {
            vTaskDelay(pdMS_TO_TICKS(100));
        }

        int analogValue = adc1_get_raw(output->pin);
        char response[100];
        snprintf(response, sizeof(response), "{\"adc_raw\":%d, \"pin\":%d}", analogValue, output->pin);

        if (output->min_adc != 0)
        {
            if (analogValue < output->min_adc)
            {
                continue;
            }
        }

        if (output->max_adc != 0)
        {
            if (analogValue > output->max_adc)
            {
                continue;
            }
        }

        publish(outputTask->config_id, response, SINGLE_ADC_SIGNAL);
    }
}
void lisening_output_pin(Message *message)
{
    if (message->message_type != CONFIG)
    {
        return;
    }
    closeAllTasks();

    for (int i = 0; i < message->analog_reader_size; i++)
    {

        ESP_LOGE("PIN", "Read from pin %d %d", message->analog_reader[i].pin, message->analog_reader[i].sampling);

        AnalogReaderTask *outputTask = deepCopyMessageToOutputTask(message, i);
        if (outputTask)
        {
            TaskHandle_t taskHandle;
            xTaskCreate(&readAnalog, "readAnalogTask", 2048, outputTask, 5, &taskHandle);
            addTask(taskHandle, outputTask);
        }
    }
}

static void addTask(TaskHandle_t task, AnalogReaderTask *outputTask)
{
    if (taskCount < MAX_TASKS)
    {
        int t = taskCount++;
        tasks[t] = task;
        outputDataTask[t] = outputTask;
    }
}

static void closeAllTasks()
{
    for (int i = 0; i < taskCount; i++)
    {
        if (tasks[i] != NULL)
        {
            vTaskDelete(tasks[i]);
            tasks[i] = NULL;
        }
        if (outputDataTask[i] != NULL)
        {
            freeOutputTask(outputDataTask[i]);
            outputDataTask[i] = NULL;
        }
    }
    taskCount = 0;
}

static AnalogReaderTask *deepCopyMessageToOutputTask(const Message *src, int outputIndex)
{
    if (outputIndex < 0 || outputIndex >= src->analog_reader_size)
    {
        return NULL;
    }

    AnalogReaderTask *copy = malloc(sizeof(AnalogReaderTask));
    if (copy)
    {
        memcpy(copy->member_key, src->member_key, sizeof(copy->member_key));
        memcpy(copy->device_key, src->device_key, sizeof(copy->device_key));
        copy->config_id = src->config_id;
        memcpy(&copy->analog_reader, &src->analog_reader[outputIndex], sizeof(AnalogConfig));
    }
    return copy;
}

static void freeOutputTask(AnalogReaderTask *task)
{
    if (task)
    {
        free(task);
    }
}