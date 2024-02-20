package pl.lasota.sensor.core.models.mqtt.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import pl.lasota.sensor.core.models.mqtt.payload.from.AnalogValuePayload;
import pl.lasota.sensor.core.models.mqtt.payload.from.ConnectDevicePayload;
import pl.lasota.sensor.core.models.mqtt.payload.to.PwmPayload;
import pl.lasota.sensor.core.models.sensor.Sensor;

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

    @JsonProperty("device_key")
    private String deviceKey;

    @JsonProperty("member_key")
    private String memberKey;

    @JsonProperty("message_type")
    private MessageType messageType;

    @JsonProperty("payload")
    private JsonNode payload;


    /**
     * @hidden
     */
    public MessageFrame(Long configId, String version, String deviceKey, String memberKey, MessageType messageType, JsonNode payload) {
        this.configIdentifier = configId;
        this.versionFirmware = version;
        this.deviceKey = deviceKey;
        this.memberKey = memberKey;
        this.messageType = messageType;
        this.payload = payload;
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public String makePayloadForDevice() throws JsonProcessingException {
        return switch (messageType) {
            case DEVICE_CONNECTED, SINGLE_ADC_SIGNAL -> throw new UnsupportedOperationException();
            case CONFIG, PWM -> om.writeValueAsString(this);
        };
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public Sensor.SensorBuilder getPayloadFromDriver() {
        return switch (messageType) {
            case DEVICE_CONNECTED -> new ConnectDevicePayload().parse(this);
            case CONFIG, PWM -> throw new UnsupportedOperationException();
            case SINGLE_ADC_SIGNAL -> new AnalogValuePayload().parse(this);
        };
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public static MessageFrame factoryConfigPayload(Long configId, String version, String deviceKey, String memberKey, String config) throws JsonProcessingException {
        return new MessageFrame(configId, version, deviceKey, memberKey, MessageType.CONFIG, om.readTree(config));
    }

    /**
     * @hidden
     */
    @JsonIgnore
    public static MessageFrame factoryPwmPayload(Long configId, String version, String deviceKey, String memberKey, PwmPayload pwmPayload) throws JsonProcessingException {
        String json = om.writeValueAsString(pwmPayload);
        return new MessageFrame(configId, version, deviceKey, memberKey, MessageType.PWM, om.readTree(json));
    }

}
