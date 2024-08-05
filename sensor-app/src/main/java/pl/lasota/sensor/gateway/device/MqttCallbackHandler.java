package pl.lasota.sensor.gateway.device;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.device.services.filters.MessageProcess;
import pl.lasota.sensor.payload.MessagePayload;

@Component
@Slf4j
public class MqttCallbackHandler implements MqttCallback {
    private final SimpleCallback connected;
    private final SimpleCallback disconnected;
    private final MessageProcess receiver;

    public MqttCallbackHandler(@Qualifier("connected") SimpleCallback connected,
                               @Qualifier("disconnected") SimpleCallback disconnected, MessageProcess receiver) {
        this.connected = connected;
        this.disconnected = disconnected;
        this.receiver = receiver;
    }

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        log.info("Disconnect");
        try {
            disconnected.handle();
        } catch (MqttException e) {
            log.error("Problem with disconnect", e);
        }
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        log.error("Occurred problem with mqtt", exception);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        byte[] payload = message.getPayload();
        String s = new String(payload);
        receiver.received(new MessagePayload(topic,s));
    }

    @Override
    public void deliveryComplete(IMqttToken token) {
        try {
            int messageId = token.getMessageId();
            log.info("Message with ID {} has been delivered successfully", messageId);
        } catch (Exception e) {
            log.error("Failed to get message ID from token", e);
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("Connected {}", reconnect);
        try {
            connected.handle();
        } catch (MqttException e) {
            log.error("Problem with connect", e);
        }
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {
//        log.error("problem {}",reasonCode);
    }
}
