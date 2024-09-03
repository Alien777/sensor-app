package pl.lasota.sensor.flow.services.keeper;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KeeperForSetUp {
    private final ConcurrentHashMap<Config, Boolean> configs = new ConcurrentHashMap<>();

    public void addPwm(String deviceId, int gpio) {
        configs.put(new Config(deviceId, gpio, TypeConfig.PWM), true);
    }

    public void addAnalog(String deviceId, int gpio) {
        configs.put(new Config(deviceId, gpio, TypeConfig.ANALOG), true);
    }

    public void addDigital(String deviceId, int gpio) {
        configs.put(new Config(deviceId, gpio, TypeConfig.DIGITAL), true);
    }

    public void removeConfig(String deviceId, int gpio) {
        configs.remove(new Config(deviceId, gpio));
    }

    public List<Integer> filter(String deviceId, TypeConfig type) {
        return configs.keySet().stream().filter(c -> deviceId.equals(c.deviceId) && type == c.type).map(config -> config.gpio).toList();
    }

    public Boolean contains(String deviceId, TypeConfig type, int gpio) {
        return configs.keySet().stream().anyMatch(c -> deviceId.equals(c.deviceId) && type == c.type && c.gpio == gpio);
    }


    @Getter
    static class Config {
        private final String deviceId;
        private final int gpio;
        private TypeConfig type;

        public Config(String deviceId, int gpio, TypeConfig type) {
            this.deviceId = deviceId;
            this.gpio = gpio;
            this.type = type;
        }

        public Config(String deviceId, int gpio) {
            this.deviceId = deviceId;
            this.gpio = gpio;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Config config = (Config) o;
            return gpio == config.gpio && Objects.equals(deviceId, config.deviceId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(deviceId, gpio);
        }
    }

    @Getter
    public enum TypeConfig {
        ANALOG,
        DIGITAL,
        PWM
    }
}



