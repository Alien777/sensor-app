package pl.lasota.sensor.bus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.bus.conventer.AudioToTextConverter;
import pl.lasota.sensor.configs.properties.AIProperties;
import pl.lasota.sensor.entities.Member;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.PipedOutputStream;

@Service
@RequiredArgsConstructor
public class AudioWaveInputStreamBus extends InputStreamBus<Member, String> {

    private final AIProperties aiProperties;

    @Override
    public InputStreamBus<Member, String>.Broadcast<PipedOutputStream, AudioInputStream> takeBroadcaster(Member streamInformation) throws IOException {
        return new Broadcast<PipedOutputStream, AudioInputStream>(() -> {
            try {
                return new AudioToTextConverter(aiProperties.getVoiceModel());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, streamInformation);
    }
}
