# Communication Contract API

This api is constant for all devices. Basic of structures of api they ensure Java classes. It is possible extend this
api
using [schema.json](../firmwares/ESP-32S/schema.json). For example attent is optional in Java class but for version
firmwares
ESP-32S schema reduces it to values 0, 1, 2, 3

AS and Device using for communication CSV format.

## MessageFrame

- **Description**: This is the base message frame.

- `device_id`:
    - **Type**: String
    - 12 chars
    - **Description**: A unique key associated with the device, used for secure identification and communication.
- `member_id`:
    - **Type**: String
    - 16 chars
    - **Description**: Specific key related to a member, used for authentication and ensuring secure access.
- `token`:
    - **Type**: UUID
    - 36 chars
    - **Description**: Unique generated token for communication
- `firmware`:
    - **Type**: String
    - 12 chars
    - **Description**: Specifies the firmwares version of the device, crucial for compatibility checks and updates.
- `config_id`:
    - **Type**: Number
    - **Description**: Unique identifier for the configuration, facilitating precise configuration management and
      retrieval.
- `request_id`:
    - **Type**: String
    - 36 chars
    - **Description**: field for identifying the query
- `message_type`:
    - **Type**: MessageType
    - **Description**: Indicates the type of message being sent, ensuring proper handling and processing.
- `payload`:
    - **Type**: JsonNode
    - 256 chars
    - csv type
    - **Description**: The content of the message, structured in a flexible JSON format to accommodate various data
      types and structures.

**Example:**
device_id;member_id;token;firmware;config_id;request_id;message_type;payload
7C9EBDF513CC;TIKDvPt3Lu0wYiNg;51c6c380-b3a6-4550-bac0-65781eb7ce0d;ESP-32S;1;51c6c380-b3a6-4550-bac0-65781eb7ce0d;CONFIG;payload

## MessageType

- `DEVICE_CONNECTED`:
    - **Type**: String
    - **Description**: Occurs when a sensor is connected to MQTT.
    - **Communication way**: DEVICE to Application Server (AS)

- `CONFIG`:
    - **Type**: String
    - **Description**: Configuration type is used to send configuration from the Application Server (AS) to the device.
    - **Communication way**: AS to DEVICE

- `ANALOG`:
    - **Type**: String
    - **Description**: Ask device for analog value
    - **Communication way**: AS to DEVICE

- `ANALOG_ACK`:
    - **Type**: String
    - **Description**: Received analog value
    - **Communication way**: DEVICE to AS

- `PWM`:
    - **Type**: String
    - **Description**: Indicates that the Application Server (AS) sends a value to the PWM pin.
    - **Communication way**: AS to DEVICE

- `PWM_ACK`:
    - **Type**: String
    - **Description**: Mean is device received the PWM message.
    - **Communication way**: DEVICE to AS

- `PING`:
    - **Type**: String
    - **Description**: AS send to Device message to pinging
    - **Communication way**: AS to DEVICE

- `PING_ACK`:
    - **Type**: String
    - **Description**: Response on the message ping
    - **Communication way**: DEVICE to AS

## Payload

### ConfigPayload (`CONFIG`)

- `analog_size_config`:
    - **Type**: Integer
    - **Description**: Size of analogs config
- `pwm_size_config`:
    - **Type**: Integer
    - **Description**: Size of PWMs config
- `digital_size_config`:
    - **Type**: Integer
    - **Description**: Size of digitals config
- `analog_config`:
    - **Type**: list of values in csv format
    - **Description**: Configuration of analog reader.
- `pwm_config`:
    - **Type**: list of values in csv format
    - **Description**: Configuration of PWM.
- `digital_config`:
    - **Type**: list of values in csv format
    - **Description**: Configuration of PWM.

device_id;member_id;token;firmware;config_id;request_id;message_type;analog_size_config;pwm_size_config;digital_size_config;analog_pin;analog_width;analog_atten;analog_pin_2;analog_width_2;analog_atten_2;digital_pin;digital_pin_2
deviceId;memberId;token;version;1;51c6c380-b3a6-4550-bac0-65781eb7ce0d;CONFIG;2;0;2;4;12;3;4;12;3;22;21;

### AnalogConfig (`CONFIG`)

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

### PwmConfig (`CONFIG`)

- `pin`:
    - **Type**: Integer
    - **Description**: Pwm pin

- `freq`:
    - **Type**: Integer
    - **Description**: Frequency of PWM

- `resolution`:
    - **Type**: Integer
    - **Description**:  Resolution of PWM

### DigitalConfig (`CONFIG`)

- `pin`:
    - **Type**: Integer
    - **Description**: Digital pin

### PwmPayload (`PWM`)

- `pin`:
    - **Type**: Integer
    - **Description**: Identifies a specific pin capable of PWM output.
    -
- `value`:
    - **Type**: Integer
    - **Description**: The PWM value to be set on the specified pin.

- `duration`:
    - **Type**: Integer
    - **Description**: Time in millisecond then after this time pwm will be set 0 on the device

### AnalogAckDataPayload (`ANALOG`)

- `pin`:
    - **Type**: Integer
    - **Description**: Represents the pin number on the device.

- `adc_raw`:
    - **Type**: Integer
    - **Description**: The analog value.

### ConnectDevicePayload (`DEVICE_CONNECTED`)

- No fields required.

### PingDataPayload (`PING`)

- No fields required.

### PingDevicePayload (`PING_ACK`)

- No fields required.

### AnalogDataPayload (`ANALOG`)

- `pin`:
    - **Type**: Integer
    - **Description**: Pin on which it will force reading
