package pl.lasota.sensor.api.apis;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.api.payload.MessageFrame;
import pl.lasota.sensor.api.process.MessageProcess;
import pl.lasota.sensor.api.properties.ApiProperties;

@RestController
@RequiredArgsConstructor
public class SensorReceiverEndpoint {
    private final MessageProcess messageProcess;
    private final ApiProperties properties;

    @RequestMapping(method = RequestMethod.POST, value = "/api/sensor/message")
    public void receiverMessage(@RequestBody MessageFrame messagePayload) {
        if (!properties.getMqtt().getUnsecureRestEndpoint()) {
            return;
        }
        messageProcess.received(messagePayload);
    }

}
