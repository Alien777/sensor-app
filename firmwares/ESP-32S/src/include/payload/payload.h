#ifndef PAYLOAD_H
#define PAYLOAD_H

#include <stdbool.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>


void analogReadSetUpConvert(ParsedMessage *mes, const char *source);

void pwmWriteSetUpConvert(ParsedMessage *mes, const char *source);

void digitalSetUpConvert(ParsedMessage *mes, const char *source);

void analogReadOneShotRequestConvert(ParsedMessage *mes, const char *source);

void digitalWriteRequestConvert(ParsedMessage *mes, const char *source);

void pwmWriteRequestConvert(ParsedMessage *mes, const char *source);

#endif // PAYLOAD_H
