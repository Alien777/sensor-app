#ifndef PAYLOAD_H
#define PAYLOAD_H

#include <stdbool.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

void configPayload(Message *mes, const char *source);
void analogExpotPayload(Message *mes, const char *source);
void digitalPayload(Message *mes, const char *source);
void pwmSetupPayload(Message *mes, const char *source);
#endif // PAYLOAD_H
