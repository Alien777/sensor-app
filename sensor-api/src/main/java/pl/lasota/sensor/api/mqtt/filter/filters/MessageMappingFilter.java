package pl.lasota.sensor.api.mqtt.filter.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.Chain;
import pl.lasota.sensor.api.mqtt.filter.Filter;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;

@Component
@Slf4j
@Scope("prototype")
public class MessageMappingFilter implements Filter<String, MessageFrame> {

    @Override
    public void execute(String request, Chain<MessageFrame> chain) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            MessageFrame messageFrame = mapper.readValue(request, MessageFrame.class);
            chain.doFilter(messageFrame);
        } catch (JsonProcessingException e) {
            log.error("Problem with parsing messageFrame {}", request, e);
        }
    }

}
