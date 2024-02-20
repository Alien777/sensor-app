# Communication Contract API

This api is constant for all devices. Basic of structures of api they ensure Java classes. It is possible extend this
api
using [schema.json](../ESP-32S/schema.json). For example attent is optional in Java class but for version firmware
ESP-32S schema reduces it to values 0, 1, 2, 3

## MessageFrame

- **Description**: This is the base message frame.
- `config_identifier`:
    - **Type**: Long
    - **Description**: Unique identifier for the configuration, facilitating precise configuration management and
      retrieval.

- `version_firmware`:
    - **Type**: String
    - **Description**: Specifies the firmware version of the device, crucial for compatibility checks and updates.

- `device_key`:
    - **Type**: String
    - **Description**: A unique key associated with the device, used for secure identification and communication.

- `member_key`:
    - **Type**: String
    - **Description**: Specific key related to a member, used for authentication and ensuring secure access.

- `message_type`:
    - **Type**: MessageType
    - **Description**: Indicates the type of message being sent, ensuring proper handling and processing.

- `payload`:
    - **Type**: JsonNode
    - **Description**: The content of the message, structured in a flexible JSON format to accommodate various data
      types and structures.

## MessageType

- `DEVICE_CONNECTED`:
    - **Type**: String
    - **Description**: Occurs when a sensor is connected to MQTT.
    - **Communication way**: DEVICE to Application Server (AS)

- `CONFIG`:
    - **Type**: String
    - **Description**: Configuration type is used to send configuration from the Application Server (AS) to the device.
    - **Communication way**: AS to DEVICE

- `SINGLE_ADC_SIGNAL`:
    - **Type**: String
    - **Description**: Indicates that an analog value has been received from the sensor.
    - **Communication way**: DEVICE to AS

- `PWM`:
    - **Type**: String
    - **Description**: Indicates that the Application Server (AS) sends a value to the PWM pin.
    - **Communication way**: AS to DEVICE

## Payload

### ConfigPayload (`CONFIG`)

- `analog_reader`:
    - **Type**: Array<AnalogConfigReader>
    - **Description**: Configuration of analog reader.

### AnalogConfigReader (`CONFIG`)

- `pin`:
    - **Type**: Integer
    - **Description**: The pin number on the microcontroller that is configured for analog reading. It specifies which
      analog input pin is being used.


- `width`:
    - **Type**: Integer
    - **Description**: The resolution of the analog-to-digital converter (ADC) for this pin, determining how finely the
      signal is quantized.

- `atten`:
    - **Type**: Integer, Optional, Null
    - **Description**: Attenuation setting for the analog pin. This refers to the reduction of the signal's amplitude,
      affecting the range of voltage the pin can read.


- `min_adc`:
    - **Type**: Integer, Optional, Null
    - **Description**: The minimum ADC value required for the signal to be sent to MQTT.

- `max_adc`:
    - **Type**: Integer, Optional, Null
    - **Description**: The maximum ADC value allowed for the signal to be sent to MQTT.

- `sampling`:
    - **Type**: Integer, Optional, Null
    - **Description**: The reading frequency in milliseconds for this analog pin.

### PwmPayload (`PWM`)

- `pin`:
    - **Type**: Integer
    - **Description**: Identifies a specific pin capable of PWM output.

- `value`:
    - **Type**: Integer
    - **Description**: The PWM value to be set on the specified pin.

### AnalogValuePayload (`SINGLE_ADC_SIGNAL`)

- `pin`:
    - **Type**: Integer
    - **Description**: Represents the pin number on the device.

- `adc_raw`:
    - **Type**: Integer
    - **Description**: The analog value.

### ConnectDevicePayload (`DEVICE_CONNECTED`)

- No fields required.
