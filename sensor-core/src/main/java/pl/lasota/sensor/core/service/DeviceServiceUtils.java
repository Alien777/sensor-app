package pl.lasota.sensor.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.exceptions.ConfigParserException;
import pl.lasota.sensor.core.exceptions.NotFoundDefaultConfigException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceConfigException;
import pl.lasota.sensor.core.exceptions.NotFoundSchemaConfigException;
import pl.lasota.sensor.core.models.device.Device;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.models.mqtt.payload.to.ConfigPayload;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@Service
public class DeviceServiceUtils {

    private final static String NAME_DEFAULT_CONFIG = "defaultConfig.json";
    private final static String NAME_SCHEMA_CONFIG = "schema.json";
    private final String firmwareFolder;

    public DeviceServiceUtils(@Value("${sensor.core.firmware}") String firmwareFolder) {
        this.firmwareFolder = firmwareFolder;
    }

    public String schemaForVersion(String schemaVersion) throws NotFoundSchemaConfigException {

        try {
            return Files.readString(Path.of(firmwareFolder, schemaVersion, NAME_SCHEMA_CONFIG));
        } catch (Exception e) {
            throw new NotFoundSchemaConfigException(e);
        }

    }

    public DeviceConfig createDefaultDeviceConfig(String version, Device device) throws NotFoundDefaultConfigException {

        ConfigPayload defaultConfig;
        String config;
        try {
            config = Files.readString(Path.of(firmwareFolder, version, NAME_DEFAULT_CONFIG));
            defaultConfig = new ObjectMapper().readValue(config, ConfigPayload.class);
        } catch (Exception e) {
            throw new NotFoundDefaultConfigException(e);
        }

        if (defaultConfig == null) {
            throw new NotFoundDefaultConfigException();
        }

        return DeviceConfig
                .builder()
                .config(defaultConfig)
                .checksum(checkSum(config + version))
                .forVersion(version)
                .time(OffsetDateTime.now())
                .device(device).build();

    }

    public void testConfigWithSchema(String jsonStr, String schemaStr) throws JsonProcessingException, ConfigParserException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(jsonStr);
        JsonNode jsonSchemaNode = objectMapper.readTree(schemaStr);

        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema schema = schemaFactory.getSchema(jsonSchemaNode);

        Set<ValidationMessage> validationResult = schema.validate(json);

        if (!validationResult.isEmpty()) {
            throw new ConfigParserException(validationResult);
        }
    }

    public long checkSum(String checksum) {
        String check = checksum.replaceAll("\\s+", "");
        Checksum crc32 = new CRC32();
        crc32.update(check.getBytes(), 0, check.getBytes().length);
        return crc32.getValue();
    }

    public DeviceConfig currentDeviceConfigCheck(Device device) throws NotFoundDeviceConfigException {
        DeviceConfig currentDeviceConfig = device.getCurrentDeviceConfig();
        if (currentDeviceConfig == null) {
            throw new NotFoundDeviceConfigException();
        }
        return currentDeviceConfig;
    }
}
