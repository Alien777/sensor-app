package pl.lasota.sensor.api.mqtt.filter.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.Chain;
import pl.lasota.sensor.api.mqtt.filter.Filter;
import pl.lasota.sensor.core.model.Message;

@Component
@Slf4j
@Scope("prototype")
public class MessageMappingFilter implements Filter<String, Message> {

    @Override
    public void execute(String request, Chain<Message> chain) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Message message = mapper.readValue(request, Message.class);
            chain.doFilter(message);
        } catch (JsonProcessingException e) {
            log.error("Problem with parsing message {}", request, e);
        }
    }

}
