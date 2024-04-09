#include "sensor_runner.h"
#include "driver/ledc.h"

#define MAX_TASKS 5

static void run_task_analog(void *pvParameters);
static AnalogTask *deepCopyMessageToOutputTask(const Message *src, int outputIndex);
static void freeOutputTask(AnalogTask *task);
static void closeAllTasks();
static void addTask(TaskHandle_t task, AnalogTask *outputTask);
static int pwm_pin_to_channel[100];

TaskHandle_t tasks[MAX_TASKS];
AnalogTask *analogDataTask[MAX_TASKS];
int pwmTasks = 0;
int taskCount = 0;

static void run_task_analog(void *pvParameters)
{
    AnalogTask *analog_task = (AnalogTask *)pvParameters;

    AnalogConfig *output = &(analog_task->analog_config);

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

        publish(analog_task->config_id, response, ANALOG);
    }
}

void config_json(Message *message)
{
    if (message->message_type != CONFIG)
    {
        ESP_LOGI("TASK", "This not config message");
        return;
    }
    ESP_LOGI("TASK", "congiggg");
    closeAllTasks();
    pwmTasks = message->pwm_configs_size;
    for (int i = 0; i < message->pwm_configs_size; i++)
    {

        ESP_LOGE("TASK", "Config pwm from pin %d %d", message->pwm_configs[i].pin, message->pwm_configs[i].channel);

        ledc_timer_config_t ledc_timer = {
            .duty_resolution = message->pwm_configs[i].resolution,
            .freq_hz = message->pwm_configs[i].freq,
            .speed_mode = LEDC_HIGH_SPEED_MODE,
            .timer_num = i};
        ledc_timer_config(&ledc_timer);

        ledc_channel_config_t ledc_channel = {
            .channel = i,
            .duty = 0,
            .gpio_num = message->pwm_configs[i].pin,
            .speed_mode = LEDC_HIGH_SPEED_MODE,
            .timer_sel = i};
        ledc_channel_config(&ledc_channel);

        pwm_pin_to_channel[message->pwm_configs[i].pin] = i;
    }
    for (int i = 0; i < message->analog_configs_size; i++)
    {

        ESP_LOGE("TASK", "Config analog from pin %d %d", message->analog_configs[i].pin, message->analog_configs[i].sampling);

        AnalogTask *outputTask = deepCopyMessageToOutputTask(message, i);
        if (outputTask)
        {
            if (outputTask->analog_config.sampling <= 0)
            {
                   adc1_config_channel_atten(outputTask->analog_config.pin, outputTask->analog_config.atten);
                   adc1_config_width(outputTask->analog_config.width);
                   continue;
            }

            TaskHandle_t taskHandle;
            xTaskCreate(&run_task_analog, "readAnalogTask", 2048, outputTask, 5, &taskHandle);
            addTask(taskHandle, outputTask);
        }
    }
}

static void addTask(TaskHandle_t task, AnalogTask *outputTask)
{
    if (taskCount < MAX_TASKS)
    {
        int t = taskCount++;
        tasks[t] = task;
        analogDataTask[t] = outputTask;
    }
}

static void closeAllTasks()
{

    for (int i = 0; i < 100; i++)
    {
        pwm_pin_to_channel[i] = -1;
    }

    for (int i = 0; i < pwmTasks; i++)
    {
        ledc_stop(LEDC_HIGH_SPEED_MODE, i, 0);

        ledc_timer_config_t ledc_timer_reset = {
            .duty_resolution = LEDC_TIMER_1_BIT,
            .freq_hz = 0,
            .speed_mode = LEDC_HIGH_SPEED_MODE,
            .timer_num = i};
        ledc_timer_config(&ledc_timer_reset);

        ledc_channel_config_t ledc_channel_reset = {
            .channel = i,
            .duty = 0,
            .gpio_num = GPIO_NUM_NC,
            .speed_mode = LEDC_HIGH_SPEED_MODE,
            .timer_sel = i};
        ledc_channel_config(&ledc_channel_reset);
    }
    pwmTasks = 0;

    for (int i = 0; i < taskCount; i++)
    {
        if (tasks[i] != NULL)
        {
            vTaskDelete(tasks[i]);
            tasks[i] = NULL;
        }
        if (analogDataTask[i] != NULL)
        {
            freeOutputTask(analogDataTask[i]);
            analogDataTask[i] = NULL;
        }
    }
    taskCount = 0;
    ESP_LOGD("CONFIG", "Cleared all tasks");
}

static AnalogTask *deepCopyMessageToOutputTask(const Message *src, int outputIndex)
{
    if (outputIndex < 0 || outputIndex >= src->analog_configs_size)
    {
        return NULL;
    }

    AnalogTask *copy = malloc(sizeof(AnalogTask));
    if (copy)
    {
        memcpy(copy->member_key, src->member_key, sizeof(copy->member_key));
        memcpy(copy->device_key, src->device_key, sizeof(copy->device_key));
        copy->config_id = src->config_id;
        memcpy(&copy->analog_config, &src->analog_configs[outputIndex], sizeof(AnalogConfig));
    }
    return copy;
}

static void freeOutputTask(AnalogTask *task)
{
    if (task)
    {
        free(task);
    }
}

void set_pwm(Message *message)
{
    if (message->message_type != PWM)
    {
        ESP_LOGD("TASK", "This not pwm message");
        return;
    }
    ESP_LOGI("PWM", "Setup duty: %d for pin: %d", message->pwn_setup.duty, message->pwn_setup.pin);
    int channel = pwm_pin_to_channel[message->pwn_setup.pin];
    ledc_set_duty(LEDC_HIGH_SPEED_MODE, channel, message->pwn_setup.duty);
    ledc_update_duty(LEDC_HIGH_SPEED_MODE, channel);
}

void analog_extort(Message *message)
{
    if (message->message_type != ANALOG_EXTORT)
    {
        ESP_LOGD("TASK", "This not analog extort message");
        return;
    }
    ESP_LOGI("ANALOG EXTORT", "For pin: %d", message->analog_read_data.pin);

    int analogValue = adc1_get_raw(message->analog_read_data.pin);
    char response[100];
    snprintf(response, sizeof(response), "{\"adc_raw\":%d, \"pin\":%d}", analogValue, message->analog_read_data.pin);
    publish(message->config_id, response, ANALOG);
}