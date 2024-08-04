package pl.lasota.sensor.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pl.lasota.sensor.payload.from.AnalogValuePayload;
import pl.lasota.sensor.payload.from.ConnectDevicePayload;
import pl.lasota.sensor.payload.from.PingAckDevicePayload;
import pl.lasota.sensor.payload.from.PwmAckDevicePayload;
import pl.lasota.sensor.payload.to.*;

import java.util.Arrays;
import java.util.UUID;

@Data
public class MessageFrame implements Parse<MessageFrame, String> {

    /**
     * @hidden
     */
    public MessageFrame() {
    }

    @JsonProperty("config_identifier")
    private Long configIdentifier;

    @JsonProperty("version_firmware")
    private String versionFirmware;//max 12 chars

    @JsonProperty("device_id")
    private String deviceId;//max 12 chars

    @JsonProperty("member_id")
    private String memberId;//max 16 chars

    @JsonProperty("token")
    private String token; // max 36 chars

    @JsonProperty("request_id")
    private String requestId;// max 36 chars

    @JsonProperty("message_type")
    private MessageType messageType; //max 20 chars

    @JsonProperty("payload")
    private String payload;


    public String getDeviceId() {
        return deviceId == null ? null : deviceId.toUpperCase();
    }

    /**
     * @hidden
     */
    private MessageFrame(Long configId, String version, String deviceId, String memberId, MessageType messageType, String token, String payload) {
        this.configIdentifier = configId;
        this.versionFirmware = version;
        this.token = token;
        this.deviceId = deviceId;
        this.memberId = memberId;
        this.messageType = messageType;
        this.payload = payload;
        this.requestId = UUID.randomUUID().toString();
    }


    /**
     * @hidden
     */
    @JsonIgnore
    public Object getPayloadFromDriver(String source) {
        return switch (messageType) {
            case DEVICE_CONNECTED -> new ConnectDevicePayload().revertConvert(source);
            case PING_ACK -> new PingAckDevicePayload().revertConvert(source);
            case PWM_ACK -> new PwmAckDevicePayload().revertConvert(source);
            case CONFIG -> new ConfigPayload().revertConvert(source);
            case ANALOG -> new ForceReadingOfAnalogDataPayload().revertConvert(source);
            case ANALOG_ACK -> new AnalogValuePayload().revertConvert(source);
            case PWM -> new PwmPayload().revertConvert(source);
            case DIGITAL_WRITE -> new DigitalPayload().revertConvert(source);
            case PING -> new PingPayload().revertConvert(source);
        };
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public static MessageFrame factoryConfigPayload(Long configId, String version, String deviceId, String memberId, String token, ConfigPayload configPayload) {
        return new MessageFrame(configId, version, deviceId, memberId, MessageType.CONFIG, token, configPayload.convert());
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public static MessageFrame factoryPwmPayload(Long configId, String version, String deviceId, String memberId, String token, PwmPayload pwmPayload) {
        return new MessageFrame(configId, version, deviceId, memberId, MessageType.PWM, token, pwmPayload.convert());
    }

    /**
     * @hidden
     */
    public static MessageFrame factoryDigitalPayload(Long configId, String version, String deviceId, String memberId, String token, DigitalPayload digitalPayload) {
        return new MessageFrame(configId, version, deviceId, memberId, MessageType.DIGITAL_WRITE, token, digitalPayload.convert());
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public static MessageFrame factorySendForAnalogData(Long configId, String version, String deviceId, String memberId,
                                                        String token, ForceReadingOfAnalogDataPayload forceReadingOfAnalogDataPayload) {
        return new MessageFrame(configId, version, deviceId, memberId, MessageType.ANALOG, token, forceReadingOfAnalogDataPayload.convert());
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public static MessageFrame factorySendPingData(Long configId, String version, String deviceId, String memberId, String token, PingPayload pingPayload) {
        return new MessageFrame(configId, version, deviceId, memberId, MessageType.PING, token, pingPayload.convert());
    }

    @Override
    public String convert() {
        return deviceId + ";"
                + memberId + ";"
                + token + ";"
                + versionFirmware + ";" +
                (configIdentifier != null ? configIdentifier : 0) + ";"
                + requestId + ";"
                + messageType.name() + ";"
                + payload;
    }

    @Override
    public MessageFrame revertConvert(String source) {
        String[] split = source.split(";");

        this.deviceId = split[0];
        this.memberId = split[1];
        this.token = split[2];
        this.versionFirmware = split[3];
        this.configIdentifier = Long.valueOf(split[4]);
        this.requestId = split[5];
        this.messageType = MessageType.valueOf(split[6]);
        this.payload =  String.join(";", Arrays.copyOfRange(split, 7, split.length));
        return this;
    }
}
