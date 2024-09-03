#include "core/message_distribute.h"

void distribute(ParsedMessage message)
{

    pwm_write_request(message, message.pwm_write_request);

    pwm_write_set_up(message, message.pwm_write_set_up);

    digital_set_up(message, message.digital_set_up);

    digital_write_request(message, message.digital_write_request);

    analog_read_one_shot_request(message, message.analog_read_one_shot_request);

    analog_read_set_up(message, message.analog_read_set_up);
}