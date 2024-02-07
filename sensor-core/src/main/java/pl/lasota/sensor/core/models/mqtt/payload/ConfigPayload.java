package pl.lasota.sensor.core.models.mqtt.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigPayload {

    @JsonProperty("output")
    private List<OutputPayload> outputs = new ArrayList<>();

    public JsonNode getJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(this);
        return objectMapper.readTree(jsonString);
    }
}
