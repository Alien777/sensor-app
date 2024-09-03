package pl.lasota.sensor.payload;

import lombok.Data;
import pl.lasota.sensor.payload.message.*;
import pl.lasota.sensor.payload.message.analog.*;
import pl.lasota.sensor.payload.message.digital.*;
import pl.lasota.sensor.payload.message.pwm.*;

import java.util.Arrays;
import java.util.UUID;

@Data

public class MessageFrame implements PayloadParser<MessageFrame, String> {

    private String deviceId;//max 12 chars

    private String memberId;//max 16 chars

    private String versionFirmware;//max 12 chars

    private UUID token; // max 36 chars

    private UUID requestId;// max 36 chars

    private PayloadType payloadType; //max 20 chars

    private PayloadParser<?, String> payload;

    public String getDeviceId() {
        return deviceId == null ? null : deviceId.toUpperCase();
    }


    public static MessageFrame of(String memberId, String deviceId, UUID token, String versionFirmware, PayloadType payloadType, PayloadParser<?, String> payload) {
        return new MessageFrame(memberId, deviceId, token, versionFirmware, UUID.randomUUID(), payloadType, payload);
    }

    private MessageFrame(String memberId, String deviceId, UUID token, String versionFirmware, UUID requestId, PayloadType payloadType, PayloadParser<?, String> payload) {
        this.deviceId = deviceId;
        this.memberId = memberId;
        this.token = token;
        this.versionFirmware = versionFirmware;
        this.requestId = requestId;
        this.payloadType = payloadType;
        this.payload = payload;
    }


    public static MessageFrame of(String source) {
        String[] split = source.split(";");
        PayloadType payloadType1 = PayloadType.valueOf(split[5]);
        PayloadParser<?, String> stringPayloadParser = serializePayload(String.join(";", Arrays.copyOfRange(split, 6, split.length)), payloadType1);
        return new MessageFrame(split[1], split[0],  UUID.fromString(split[2]), split[3], UUID.fromString(split[4]), payloadType1, stringPayloadParser);
    }

    @Override
    public String convert() {
        return deviceId + ";"
                + memberId + ";"
                + token + ";"
                + versionFirmware + ";"
                + requestId + ";"
                + payloadType.name() + ";"
                + payload.convert();
    }

    @Override
    public MessageFrame revertConvert(String source) {
        return of(source);
    }


    private static PayloadParser<?, String> serializePayload(String source, PayloadType payloadType) {
        // Handle any remaining cases
        return switch (payloadType) {
            case PWM_WRITE_REQUEST -> PwmWriteRequest.of(source);
            case PING -> Ping.of(source);
            case CONFIG -> Config.of(source);
            case CONNECTED_ACK -> ConnectedAck.of(source);
            case PING_ACK -> PingAck.of(source);
            case CONFIG_ACK -> ConfigAck.of(source);
            case PWM_WRITE_SET_UP -> PwmWriteSetUp.of(source);
            case PWM_WRITE_SET_UP_ACK -> PwmWriteSetUpAck.of(source);
            case PWM_WRITE_TEAR_DOWN -> PwmWriteTearDown.of(source);
            case PWM_WRITE_TEAR_DOWN_ACK -> PwmWriteTearDownAck.of(source);
            case PWM_WRITE_RESPONSE -> PwmWriteResponse.of(source);
            case ANALOG_READ_SET_UP -> AnalogReadSetUp.of(source);
            case ANALOG_READ_SET_UP_ACK -> AnalogReadSetUpAck.of(source);
            case ANALOG_READ_TEAR_DOWN -> AnalogReadTearDown.of(source);
            case ANALOG_READ_TEAR_DOWN_ACK -> AnalogReadTearDownAck.of(source);
            case ANALOG_READ_ONE_SHOT_REQUEST -> AnalogReadOneShotRequest.of(source);
            case ANALOG_READ_ONE_SHOT_RESPONSE -> AnalogReadOneShotResponse.of(source);
            case ANALOG_READ_CONTINOUS_REQUEST -> AnalogReadContinuousRequest.of(source);
            case ANALOG_READ_CONTINOUS_RESPONSE -> AnalogReadContinuousResponse.of(source);
            case DIGITAL_SET_UP -> DigitalSetUp.of(source);
            case DIGITAL_SET_UP_ACK -> DigitalSetUpAck.of(source);
            case DIGITAL_TEAR_DOWN -> DigitalTearDown.of(source);
            case DIGITAL_TEAR_DOWN_ACK -> DigitalTearDownAck.of(source);
            case DIGITAL_WRITE_REQUEST -> DigitalWriteRequest.of(source);
            case DIGITAL_WRITE_RESPONSE -> DigitalWriteResponse.of(source);
            case DIGITAL_READ_ONE_SHOT_REQUEST -> DigitalReadOneShotRequest.of(source);
            case DIGITAL_READ_ONE_SHOT_RESPONSE -> DigitalReadOneShotResponse.of(source);
            case DIGITAL_READ_CONTINOUS_REQUEST -> DigitalReadContinuousRequest.of(source);
            case DIGITAL_READ_CONTINOUS_RESPONSE -> DigitalReadContinuousResponse.of(source);
            // Handle any remaining cases
            default -> throw new IllegalArgumentException("Unsupported payload type: " + payloadType);
        };
    }


}
