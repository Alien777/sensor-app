package pl.lasota.sensor.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @JsonProperty("device_key")
    private String deviceKey;

    @JsonProperty("member_key")
    private String memberKey;

    @JsonProperty("message_type")
    private MessageType messageType;

    @JsonProperty("message")
    private String message;


    @JsonIgnore
    public String toRawJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String messageJson = objectMapper.writeValueAsString(Message.create(deviceKey, memberKey, messageType, "{messageReplace}"));
        return messageJson.replaceAll("\"\\{messageReplace}\"", message);
    }

    @JsonIgnore
    public static Message create(String deviceKey, String memberKey, MessageType messageType, String message) {
        return new Message(deviceKey, memberKey, messageType, message);
    }
}
