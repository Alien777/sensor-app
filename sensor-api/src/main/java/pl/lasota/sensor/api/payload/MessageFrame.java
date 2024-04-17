package pl.lasota.sensor.api.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import pl.lasota.sensor.api.entities.Sensor;
import pl.lasota.sensor.api.payload.from.AnalogValuePayload;
import pl.lasota.sensor.api.payload.from.ConnectDevicePayload;
import pl.lasota.sensor.api.payload.to.ForceReadingOfAnalogDataPayload;
import pl.lasota.sensor.api.payload.to.PwmPayload;

@Data
public class MessageFrame {

    /**
     * @hidden
     */
    public MessageFrame() {
    }

    /**
     * @hidden
     */
    @JsonIgnore
    private static final ObjectMapper om = new ObjectMapper();

    @JsonProperty("config_identifier")
    private Long configIdentifier;

    @JsonProperty("version_firmware")
    private String versionFirmware;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("member_id")
    private String memberId;

    @JsonProperty("token")
    private String token;

    @JsonProperty("message_type")
    private MessageType messageType;

    @JsonProperty("payload")
    private JsonNode payload;

    public String getDeviceId() {
        return deviceId.toUpperCase();
    }

    /**
     * @hidden
     */
    public MessageFrame(Long configId, String version, String deviceId, String memberId, MessageType messageType, String token, JsonNode payload) {
        this.configIdentifier = configId;
        this.versionFirmware = version;
        this.token = token;
        this.deviceId = deviceId;
        this.memberId = memberId;
        this.messageType = messageType;
        this.payload = payload;
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public String makePayloadForDevice() throws JsonProcessingException {
        return switch (messageType) {
            case DEVICE_CONNECTED, ANALOG -> throw new UnsupportedOperationException();
            case CONFIG, PWM, ANALOG_EXTORT -> om.writeValueAsString(this);
        };
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public Sensor.SensorBuilder getPayloadFromDriver() {
        return switch (messageType) {
            case DEVICE_CONNECTED -> new ConnectDevicePayload().parse(this);
            case CONFIG, PWM, ANALOG_EXTORT -> throw new UnsupportedOperationException();
            case ANALOG -> new AnalogValuePayload().parse(this);
        };
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public static MessageFrame factoryConfigPayload(Long configId, String version, String deviceId, String memberId, String token, String config) throws JsonProcessingException {
        return new MessageFrame(configId, version, deviceId, memberId, MessageType.CONFIG, token, om.readTree(config));
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public static MessageFrame factoryPwmPayload(Long configId, String version, String deviceId, String memberId, String token, PwmPayload pwmPayload) throws JsonProcessingException {
        String json = om.writeValueAsString(pwmPayload);
        return new MessageFrame(configId, version, deviceId, memberId, MessageType.PWM, token, om.readTree(json));
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public static MessageFrame factorySendForAnalogData(Long configId, String version, String deviceId, String memberId, String token, ForceReadingOfAnalogDataPayload analogData) throws JsonProcessingException {
        String json = om.writeValueAsString(analogData);
        return new MessageFrame(configId, version, deviceId, memberId, MessageType.ANALOG_EXTORT, token, om.readTree(json));
    }

}
