package pl.lasota.sensor.configs;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import pl.lasota.sensor.gateway.gui.websocket.AudioStreamHandler;
import pl.lasota.sensor.security.AuthService;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final AuthService authService;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(new AudioStreamHandler(authService), "/api/socket/audio-commend")
                .setAllowedOriginPatterns("*");
    }
}