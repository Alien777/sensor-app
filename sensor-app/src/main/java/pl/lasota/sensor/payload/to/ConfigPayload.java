package pl.lasota.sensor.payload.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pl.lasota.sensor.payload.Parse;
import pl.lasota.sensor.payload.to.dependet.AnalogConfig;
import pl.lasota.sensor.payload.to.dependet.DigitalConfig;
import pl.lasota.sensor.payload.to.dependet.PwmConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class ConfigPayload implements Parse<ConfigPayload, String> {

    public ConfigPayload() {
    }

    @JsonProperty("analog_configs")
    private List<AnalogConfig> analogReader = new ArrayList<>();

    @JsonProperty("pwm_configs")
    private List<PwmConfig> pwmConfig = new ArrayList<>();

    @JsonProperty("digital_configs")
    private List<DigitalConfig> digitalConfig = new ArrayList<>();

    @Override
    public String convert() {

        StringBuilder sb = new StringBuilder();
        sb.append(analogReader.size()).append(";").append(pwmConfig.size()).append(";").append(digitalConfig.size()).append(";");

        for (AnalogConfig analogConfig : analogReader) {
            sb.append(analogConfig.convert()).append(";");
        }
        // Adding pwm configs
        for (PwmConfig pwmConfig : pwmConfig) {
            sb.append(pwmConfig.convert()).append(";");
        }

        // Adding digital configs
        for (DigitalConfig digitalConfig : digitalConfig) {
            sb.append(digitalConfig.convert()).append(";");
        }

        return sb.toString();
    }

    @Override
    public ConfigPayload revertConvert(String source) {
        String[] lines = source.split(";");
        analogReader.clear();
        pwmConfig.clear();
        digitalConfig.clear();

        int analogReaderSize = Integer.parseInt(lines[0]);
        int pwmConfigSize = Integer.parseInt(lines[1]);
        int digitalConfigSize = Integer.parseInt(lines[2]);

        int index = 3;

        for (int i = 0; i < analogReaderSize; i++) {
            String[] analogConfigData = Arrays.copyOfRange(lines, index, index + AnalogConfig.length());
            AnalogConfig analogConfig = new AnalogConfig();
            analogConfig.revertConvert(String.join(";", analogConfigData));
            analogReader.add(analogConfig);
            index += AnalogConfig.length();
        }


        for (int i = 0; i < pwmConfigSize; i++) {
            String[] pwmConfigData = Arrays.copyOfRange(lines, index, index + PwmConfig.length());
            PwmConfig pwmConfigItem = new PwmConfig();
            pwmConfigItem.revertConvert(String.join(";", pwmConfigData));
            pwmConfig.add(pwmConfigItem);
            index += PwmConfig.length();
        }

        for (int i = 0; i < digitalConfigSize; i++) {
            String[] digitalConfigData = Arrays.copyOfRange(lines, index, index + DigitalConfig.length());
            DigitalConfig digitalConfigItem = new DigitalConfig();
            digitalConfigItem.revertConvert(String.join(";", digitalConfigData));
            digitalConfig.add(digitalConfigItem);
            index += DigitalConfig.length();
        }

        return this;
    }
}
