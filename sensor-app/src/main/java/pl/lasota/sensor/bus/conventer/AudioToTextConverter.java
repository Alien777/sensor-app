package pl.lasota.sensor.bus.conventer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.util.function.Consumer;


public class AudioToTextConverter implements Converter<String, AudioInputStream> {


    public static final int BUFFER_SIZE = 2048;
    public static final String FIELD_NAME = "partial";

    private final ObjectMapper om = new ObjectMapper();
    private final Recognizer recognizer;
    private String last = "";

    public AudioToTextConverter(String modelPath, int sampleRate) throws IOException {
        Model model = new Model(modelPath);
        recognizer = new Recognizer(model, sampleRate);
    }

    byte[] buffer = new byte[BUFFER_SIZE];

    @Override
    public void onConverter(AudioInputStream in, Consumer<String> consumer) throws Exception {


        int bytesRead = in.read(buffer, 0, buffer.length);
        if (bytesRead == -1) {
            return;
        }
        if (!recognizer.acceptWaveForm(buffer, bytesRead)) {
            String partialResult = recognizer.getPartialResult();
            if (!last.equals(partialResult)) {
                last = partialResult;
                JsonNode jsonNode = om.readTree(partialResult);
                String text = jsonNode.get(FIELD_NAME).asText();
                if (text.trim().isBlank()) {
                    return;
                }
                System.out.println(text);
                consumer.accept(text);
            }
        } else {
            recognizer.getResult();
        }
    }
}

