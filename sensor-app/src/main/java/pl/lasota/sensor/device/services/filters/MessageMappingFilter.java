package pl.lasota.sensor.device.services.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.payload.MessageFrame;

@Component
@Slf4j
@Scope("prototype")
public class MessageMappingFilter implements Filter<String, MessageFrame> {

    @Override
    public void execute(String request, FilterContext context, Chain<MessageFrame> chain) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MessageFrame messageFrame = mapper.readValue(request, MessageFrame.class);
        chain.doFilter(messageFrame);
    }

}