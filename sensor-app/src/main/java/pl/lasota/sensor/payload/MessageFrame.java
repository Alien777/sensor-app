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

    public static MessageFrame of(String memberId, String deviceId, UUID token, String versionFirmware, PayloadParser<?, String> payload) {
        return new MessageFrame(memberId, deviceId, token, versionFirmware, UUID.randomUUID(), deserializePayload(payload), payload);
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
        return new MessageFrame(split[1], split[0], UUID.fromString(split[2]), split[3], UUID.fromString(split[4]), payloadType1, stringPayloadParser);
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


    public static <T extends PayloadParser<T, String>> PayloadParser<T, String> serializePayload(String source, PayloadType payloadType) {
        return switch (payloadType) {
            case PWM_WRITE_REQUEST -> (PayloadParser<T, String>) PwmWriteRequest.of(source);
            case PING -> (PayloadParser<T, String>) Ping.of(source);
            case CONFIG -> (PayloadParser<T, String>) Config.of(source);
            case CONNECTED_ACK -> (PayloadParser<T, String>) ConnectedAck.of(source);
            case PING_ACK -> (PayloadParser<T, String>) PingAck.of(source);
            case CONFIG_ACK -> (PayloadParser<T, String>) ConfigAck.of(source);
            case PWM_WRITE_SET_UP -> (PayloadParser<T, String>) PwmWriteSetUp.of(source);
            case PWM_WRITE_SET_UP_ACK -> (PayloadParser<T, String>) PwmWriteSetUpAck.of(source);
            case PWM_WRITE_TEAR_DOWN -> (PayloadParser<T, String>) PwmWriteTearDown.of(source);
            case PWM_WRITE_TEAR_DOWN_ACK -> (PayloadParser<T, String>) PwmWriteTearDownAck.of(source);
            case PWM_WRITE_RESPONSE -> (PayloadParser<T, String>) PwmWriteResponse.of(source);
            case ANALOG_READ_SET_UP -> (PayloadParser<T, String>) AnalogReadSetUp.of(source);
            case ANALOG_READ_SET_UP_ACK -> (PayloadParser<T, String>) AnalogReadSetUpAck.of(source);
            case ANALOG_READ_TEAR_DOWN -> (PayloadParser<T, String>) AnalogReadTearDown.of(source);
            case ANALOG_READ_TEAR_DOWN_ACK -> (PayloadParser<T, String>) AnalogReadTearDownAck.of(source);
            case ANALOG_READ_ONE_SHOT_REQUEST -> (PayloadParser<T, String>) AnalogReadOneShotRequest.of(source);
            case ANALOG_READ_ONE_SHOT_RESPONSE -> (PayloadParser<T, String>) AnalogReadOneShotResponse.of(source);
            case ANALOG_READ_CONTINOUS_REQUEST -> (PayloadParser<T, String>) AnalogReadContinuousRequest.of(source);
            case ANALOG_READ_CONTINOUS_RESPONSE -> (PayloadParser<T, String>) AnalogReadContinuousResponse.of(source);
            case DIGITAL_SET_UP -> (PayloadParser<T, String>) DigitalSetUp.of(source);
            case DIGITAL_SET_UP_ACK -> (PayloadParser<T, String>) DigitalSetUpAck.of(source);
            case DIGITAL_TEAR_DOWN -> (PayloadParser<T, String>) DigitalTearDown.of(source);
            case DIGITAL_TEAR_DOWN_ACK -> (PayloadParser<T, String>) DigitalTearDownAck.of(source);
            case DIGITAL_WRITE_REQUEST -> (PayloadParser<T, String>) DigitalWriteRequest.of(source);
            case DIGITAL_WRITE_RESPONSE -> (PayloadParser<T, String>) DigitalWriteResponse.of(source);
            case DIGITAL_READ_ONE_SHOT_REQUEST -> (PayloadParser<T, String>) DigitalReadOneShotRequest.of(source);
            case DIGITAL_READ_ONE_SHOT_RESPONSE -> (PayloadParser<T, String>) DigitalReadOneShotResponse.of(source);
            case DIGITAL_READ_CONTINOUS_REQUEST -> (PayloadParser<T, String>) DigitalReadContinuousRequest.of(source);
            case DIGITAL_READ_CONTINOUS_RESPONSE -> (PayloadParser<T, String>) DigitalReadContinuousResponse.of(source);
            default -> throw new IllegalArgumentException("Unsupported payload type: " + payloadType);
        };
    }

    public static PayloadType deserializePayload(PayloadParser<?, String> parser) {
        return switch (parser) {
            case PwmWriteRequest p -> PayloadType.PWM_WRITE_REQUEST;
            case Ping p -> PayloadType.PING;
            case Config p -> PayloadType.CONFIG;
            case ConnectedAck p -> PayloadType.CONNECTED_ACK;
            case PingAck p -> PayloadType.PING_ACK;
            case ConfigAck p -> PayloadType.CONFIG_ACK;
            case PwmWriteSetUp p -> PayloadType.PWM_WRITE_SET_UP;
            case PwmWriteSetUpAck p -> PayloadType.PWM_WRITE_SET_UP_ACK;
            case PwmWriteTearDown p -> PayloadType.PWM_WRITE_TEAR_DOWN;
            case PwmWriteTearDownAck p -> PayloadType.PWM_WRITE_TEAR_DOWN_ACK;
            case PwmWriteResponse p -> PayloadType.PWM_WRITE_RESPONSE;
            case AnalogReadSetUp p -> PayloadType.ANALOG_READ_SET_UP;
            case AnalogReadSetUpAck p -> PayloadType.ANALOG_READ_SET_UP_ACK;
            case AnalogReadTearDown p -> PayloadType.ANALOG_READ_TEAR_DOWN;
            case AnalogReadTearDownAck p -> PayloadType.ANALOG_READ_TEAR_DOWN_ACK;
            case AnalogReadOneShotRequest p -> PayloadType.ANALOG_READ_ONE_SHOT_REQUEST;
            case AnalogReadOneShotResponse p -> PayloadType.ANALOG_READ_ONE_SHOT_RESPONSE;
            case AnalogReadContinuousRequest p -> PayloadType.ANALOG_READ_CONTINOUS_REQUEST;
            case AnalogReadContinuousResponse p -> PayloadType.ANALOG_READ_CONTINOUS_RESPONSE;
            case DigitalSetUp p -> PayloadType.DIGITAL_SET_UP;
            case DigitalSetUpAck p -> PayloadType.DIGITAL_SET_UP_ACK;
            case DigitalTearDown p -> PayloadType.DIGITAL_TEAR_DOWN;
            case DigitalTearDownAck p -> PayloadType.DIGITAL_TEAR_DOWN_ACK;
            case DigitalWriteRequest p -> PayloadType.DIGITAL_WRITE_REQUEST;
            case DigitalWriteResponse p -> PayloadType.DIGITAL_WRITE_RESPONSE;
            case DigitalReadOneShotRequest p -> PayloadType.DIGITAL_READ_ONE_SHOT_REQUEST;
            case DigitalReadOneShotResponse p -> PayloadType.DIGITAL_READ_ONE_SHOT_RESPONSE;
            case DigitalReadContinuousRequest p -> PayloadType.DIGITAL_READ_CONTINOUS_REQUEST;
            case DigitalReadContinuousResponse p -> PayloadType.DIGITAL_READ_CONTINOUS_RESPONSE;
            default -> throw new IllegalArgumentException("Unsupported parser type: " + parser.getClass());
        };
    }


}
