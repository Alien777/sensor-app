package pl.lasota.sensor.gateway.gui.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.lasota.sensor.bus.AudioWaveInputStreamBus;
import pl.lasota.sensor.bus.InputStreamBus;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.security.AuthService;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AudioStreamHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<WebSocketSession, InputStreamBus<Member, String>.Broadcast<PipedOutputStream, AudioInputStream>>
            currentActiveSession = new ConcurrentHashMap<>();

    private final AudioWaveInputStreamBus audioWaveInputStreamBus;
    private final AuthService authService;

    public AudioStreamHandler(AudioWaveInputStreamBus audioWaveInputStreamBus, AuthService authService) throws Exception {
        this.audioWaveInputStreamBus = audioWaveInputStreamBus;
        this.authService = authService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, String> payload = new ObjectMapper().readValue(message.getPayload(), Map.class);
        try {
            Member member = authService.checkAuthToken(payload);
            currentActiveSession.put(session, audioWaveInputStreamBus.takeBroadcaster(member));
        } catch (Exception e) {
            log.error("Problem with login {}", message.getPayload(), e);
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        InputStreamBus<Member, String>.Broadcast<PipedOutputStream, AudioInputStream> broadcast = currentActiveSession.get(session);
        if (broadcast == null) {
            return;
        }

        ByteBuffer payload = message.getPayload();
        byte[] audioData = new byte[payload.remaining()];
        payload.get(audioData);
        try {
            broadcast.streamOut.write(audioData);
        } catch (IOException e) {

        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        InputStreamBus<Member, String>.Broadcast<PipedOutputStream, AudioInputStream> broadcast = currentActiveSession.get(session);
        if (broadcast == null) {
            super.afterConnectionClosed(session, status);
            return;
        }
        broadcast.close();
        currentActiveSession.remove(session);
        super.afterConnectionClosed(session, status);
    }
}


