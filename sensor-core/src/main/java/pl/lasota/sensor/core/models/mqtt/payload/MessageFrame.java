package pl.lasota.sensor.core.models.mqtt.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lasota.sensor.core.models.MessageType;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.models.sensor.SingleAdcSignal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageFrame {
    @JsonIgnore
    private static final ObjectMapper om = new ObjectMapper();
    @JsonIgnore
    private static final String ADC_RAW = "adc_raw";
    @JsonIgnore
    private static final String PIN = "pin";

    @JsonProperty("device_key")
    private String deviceKey;

    @JsonProperty("member_key")
    private String memberKey;

    @JsonProperty("message_type")
    private MessageType messageType;

    @JsonProperty("payload")
    private JsonNode payload;


    @JsonIgnore
    public String toJson() throws JsonProcessingException {

        return switch (messageType) {
            case DEVICE_CONNECTED, SINGLE_ADC_SIGNAL -> throw new UnsupportedOperationException();
            case CONFIG -> om.writeValueAsString(this);
        };
    }

    @JsonIgnore
    public static MessageFrame createConfigPayload(String deviceKey, String memberKey, String payload) throws JsonProcessingException {
        return new MessageFrame(deviceKey, memberKey, MessageType.CONFIG, om.readTree(payload));
    }

    @JsonIgnore
    public Sensor.SensorBuilder getSingleAdcSignal() {
        double adcRaw = this.getPayload().findValue(ADC_RAW).asDouble();
        int pin = this.getPayload().findValue(PIN).asInt();

        return SingleAdcSignal.builder()
                .pin(pin)
                .adcRaw(adcRaw);
    }


}
