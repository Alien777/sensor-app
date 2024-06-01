package pl.lasota.sensor.gateway.gui.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.security.AuthService;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;


public class AudioStreamHandler extends TextWebSocketHandler {

    private final AuthService authService;

    private ByteArrayOutputStream byteArrayOutputStream;

    public AudioStreamHandler(AuthService authService) {
        this.authService = authService;
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, String> payload = new ObjectMapper().readValue(message.getPayload(), Map.class);
        try {
            authService.checkAuth(payload, session.getId());
        } catch (Exception e) {
            authService.logout(session);
        }
    }


    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        Member user = authService.getSessionUser(session);
        if (user == null) {
            return;
        }
        ByteBuffer payload = message.getPayload();
        byte[] audioData = new byte[payload.remaining()];
        payload.get(audioData);
        try {
            byteArrayOutputStream.write(audioData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        byteArrayOutputStream.close();
        byte[] audioBytes = byteArrayOutputStream.toByteArray();

        convertRawToWav(audioBytes, new File("received_audio.wav"), 44100, 1); // 44100 Hz, mono
    }

    private void convertRawToWav(byte[] rawData, File wavFile, float sampleRate, int channels) {
        try {
            AudioFormat format = new AudioFormat(sampleRate, 16, channels, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(rawData);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, rawData.length / format.getFrameSize());

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


