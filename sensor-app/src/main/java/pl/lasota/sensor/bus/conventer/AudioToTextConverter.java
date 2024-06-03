package pl.lasota.sensor.bus.conventer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.function.Consumer;


public class AudioToTextConverter implements Converter<String, PipedOutputStream, AudioInputStream> {

    public static final int SAMPLE_RATE = 44100;
    public static final int SAMPLE_SIZE_IN_BITS = 16;
    public static final int CHANNELS = 1;
    public static final int BUFFER_SIZE = 2048;
    public static final String FIELD_NAME = "partial";

    private final ObjectMapper om = new ObjectMapper();
    private final Recognizer recognizer;
    private String last = "";

    public AudioToTextConverter(String modelPath) throws IOException {
        Model model = new Model(modelPath);
        recognizer = new Recognizer(model, SAMPLE_RATE);
    }

    @Override
    public void onConverter(AudioInputStream in, Consumer<String> consumer) throws Exception {

        byte[] buffer = new byte[BUFFER_SIZE];

        int bytesRead = in.read(buffer, 0, buffer.length);
        if (bytesRead == -1) {
            return;
        }
        if (!recognizer.acceptWaveForm(buffer, bytesRead)) {
            String partialResult = recognizer.getPartialResult();
            if (!last.equals(partialResult)) {
                last = partialResult;
                JsonNode jsonNode = om.readTree(partialResult);
                consumer.accept(jsonNode.get(FIELD_NAME).asText());
            }
        } else {
            recognizer.getResult();
        }
    }

    @Override
    public AudioInputStream wrapperInputStream(PipedInputStream pipedOutputStream) {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, true, false);
        return new AudioInputStream(pipedOutputStream, format, Long.MAX_VALUE);
    }

}

