package pl.lasota.sensor.api.mqtt.filter.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.filter.Chain;
import pl.lasota.sensor.api.mqtt.filter.Filter;
import pl.lasota.sensor.core.model.Message;

@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class CheckCorrectJsonMessageFilter implements Filter<Message, Message> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void execute(Message request, Chain<Message> chain) {
        if (isValidJson(request.getMessage())) {
            chain.doFilter(request);
        }
    }

    private boolean isValidJson(String jsonStr) {
        try {
            mapper.readTree(jsonStr);
            return true; // JSON jest prawidłowy
        } catch (JsonProcessingException e) {
            return false; // JSON jest nieprawidłowy
        }
    }

}
