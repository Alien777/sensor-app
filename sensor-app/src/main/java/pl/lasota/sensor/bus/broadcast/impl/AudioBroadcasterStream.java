package pl.lasota.sensor.bus.broadcast.impl;

import pl.lasota.sensor.bus.broadcast.BroadcastStream;
import pl.lasota.sensor.bus.conventer.AudioToTextConverter;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.exceptions.SensorBusException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.Queue;
import java.util.function.BiConsumer;

public class AudioBroadcasterStream extends BroadcastStream<String, Member, PipedOutputStream, AudioInputStream> {
    public static final int SAMPLE_RATE = 44100;
    public static final int SAMPLE_SIZE_IN_BITS = 16;
    public static final int CHANNELS = 1;

    public AudioBroadcasterStream(String model, Queue<BiConsumer<Member, String>> consumers, Member member) throws SensorBusException {
        super(pipedOutputStream -> pipedOutputStream, pipedInputStream -> {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, true, false);
            return new AudioInputStream(pipedInputStream, format, Long.MAX_VALUE);
        }, consumers, () -> {
            try {
                return new AudioToTextConverter(model, SAMPLE_RATE);
            } catch (IOException e) {
                throw new SensorBusException(e);
            }
        }, member);
    }
}
