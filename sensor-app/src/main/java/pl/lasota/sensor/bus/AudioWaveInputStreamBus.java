package pl.lasota.sensor.bus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.bus.broadcast.CustomerBroadcast;
import pl.lasota.sensor.bus.broadcast.impl.AudioBroadcasterStream;
import pl.lasota.sensor.configs.properties.AIProperties;
import pl.lasota.sensor.entities.Member;

@Service
@RequiredArgsConstructor
public class AudioWaveInputStreamBus extends CustomerBroadcast<Member, String> {

    private final AIProperties aiProperties;

    @Override
    public AudioBroadcasterStream takeBroadcaster(Member member) throws Exception {
        return new AudioBroadcasterStream(aiProperties.getVoiceModel(), getCustomers(), member);
    }
}
