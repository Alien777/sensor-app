## Message Frame

- **Description**: This is the base structure of the message used for communication between the device and the
  Application Server.

### Fields:

- `device_id`:
    - **Type**: String
    - **Length**: 12 characters
    - **Description**: A unique identifier associated with the device, used for secure identification and communication.

- `member_id`:
    - **Type**: String
    - **Length**: 16 characters
    - **Description**: A unique identifier for the member, used for authentication and access control.

- `token`:
    - **Type**: UUID
    - **Length**: 36 characters
    - **Description**: A unique token generated for secure communication between the device and the AS.

- `version_firmware`:
    - **Type**: String
    - **Length**: 12 characters
    - **Description**: Specifies the firmware version of the device, used for compatibility checks and updates.

- `request_id`:
    - **Type**: String
    - **Length**: 36 characters
    - **Description**: A unique identifier for the request.

- `message_type`:
    - **Type**: Enum
    - **Description**: Indicates the type of message being sent, which helps in proper message processing.

- `payload`:
    - **Type**: CSV
    - **Length**: Up to 256 characters
    - **Description**: Contains the message content, structured as CSV format for transmission.

**Example Message:**
device_id;member_id;token;version_firmware;request_id;message_type;payload_of_message_type
7C9EBDF513CC;TIKDvPt3Lu0wYiNg;51c6c380-b3a6-4550-bac0-65781eb7ce0d;ESP-32S;51c6c380-b3a6-4550-bac0-65781eb7ce0d;ANALOG_READ_SET_UP;23;12
---

## Message Types

### `CONNECTED_ACK`

- **Description**: Sent when a sensor successfully connects to the MQTT broker.
- **Communication Direction**: Device → Application Server
- **Payload**: Empty

### `PING`

- **Description**: Sent to ping a device and ensure it is connected.
- **Communication Direction**: Application Server → Device
- **Payload**: Empty

### `PING_ACK`

- **Description**: Sent by the device to acknowledge a PING.
- **Communication Direction**: Device → Application Server
- **Payload**: Empty

### `CONFIG`

- **Description**: Configuration message sent from the AS to the device to configure its settings.
- **Communication Direction**: Application Server → Device
- **Payload**: Empty

### `CONFIG_ACK`

- **Description**: Sent by the device to acknowledge a CONFIG.
- **Communication Direction**: Application Server → Device
- **Payload**: Empty

### `ANALOG_READ_SET_UP`

- **Description**: Sets up an analog read on the device.
- **Communication Direction**: Device → Application Server
- **Fields**:
    - `gpio` (Integer): The GPIO pin number to read from.
    - `width` (Integer): The width parameter for the analog read.

### `ANALOG_READ_SET_UP_ACK`

- **Description**: Acknowledges the setup of an analog read.
- **Communication Direction**: Application Server → Device
- **Payload**: Empty

### `ANALOG_READ_ONE_SHOT_REQUEST`

- **Description**: Requests a one-time analog read from the device.
- **Communication Direction**: Application Server → Device
- **Fields**:
    - `gpio` (Integer): The GPIO pin to perform the read on.

### `ANALOG_READ_ONE_SHOT_RESPONSE`

- **Description**: Response to a one-shot analog read request, providing the read value.
- **Communication Direction**: Device → Application Server
- **Fields**:
    - `gpio` (Integer): The GPIO pin.
    - `value` (Integer): The value read from the GPIO pin.

### `PWM_WRITE_SET_UP`

- **Description**: Configures a PWM signal on a specified GPIO pin.
- **Communication Direction**: Device → Application Server
- **Fields**:
    - `gpio` (Integer): The GPIO pin.
    - `frequency` (Integer): The frequency of the PWM signal.
    - `resolution` (Integer): The resolution of the PWM signal.
    - `duty` (Integer): The duty cycle of the PWM signal.

### `PWM_WRITE_SET_UP_ACK`

- **Description**: Acknowledges the setup of a PWM signal.
- **Communication Direction**: Application Server → Device
- **Payload**: Empty

### `PWM_WRITE_REQUEST`

- **Description**: Requests a PWM signal to be written to a GPIO pin.
- **Communication Direction**: Device → Application Server
- **Fields**:
    - `gpio` (Integer): The GPIO pin.
    - `duty` (Integer): The duty cycle.
    - `duration` (Integer): The duration to apply the duty cycle.

### `PWM_WRITE_RESPONSE`

- **Description**: Acknowledges a PWM write request.
- **Communication Direction**: Device → Application Server
- **Payload**: Empty

### `DIGITAL_SET_UP`

- **Description**: Sets up a digital pin on the device.
- **Communication Direction**: Device → Application Server
- **Fields**:
    - `gpio` (Integer): The GPIO pin.
    - `mode` (Integer): The mode for the GPIO pin.

### `DIGITAL_SET_UP_ACK`

- **Description**: Acknowledges the setup of a digital pin.
- **Communication Direction**: Application Server → Device
- **Payload**: Empty

### `DIGITAL_WRITE_REQUEST`

- **Description**: Requests a digital write on a GPIO pin.
- **Communication Direction**: Device → Application Server
- **Fields**:
    - `gpio` (Integer): The GPIO pin.
    - `level` (Integer): The digital level (HIGH or LOW).

### `DIGITAL_WRITE_RESPONSE`

- **Description**: Acknowledges a digital write request.
- **Communication Direction**: Device → Application Server
- **Payload**: Empty