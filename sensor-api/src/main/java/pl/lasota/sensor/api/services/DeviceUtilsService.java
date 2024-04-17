package pl.lasota.sensor.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.api.entities.Device;
import pl.lasota.sensor.api.entities.DeviceConfig;
import pl.lasota.sensor.api.exception.SensorApiException;
import pl.lasota.sensor.api.payload.to.ConfigPayload;
import pl.lasota.sensor.api.properties.ApiProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@Service
@RequiredArgsConstructor
public class DeviceUtilsService {

    private final static String NAME_DEFAULT_CONFIG = "defaultConfig.json";
    private final static String NAME_SCHEMA_CONFIG = "schema.json";

    private final ApiProperties properties;


    public String schemaForVersion(String schemaVersion) {

        try {
            String firmwareFolder = properties.getFirmwareFolder();
            return Files.readString(Path.of(firmwareFolder, schemaVersion, NAME_SCHEMA_CONFIG));
        } catch (Exception e) {
            throw new SensorApiException("Occurred problem with read schema", e);
        }

    }

    public DeviceConfig createDefaultDeviceConfig(String version, Device device) {

        String config;
        try {
            String firmwareFolder = properties.getFirmwareFolder();
            config = Files.readString(Path.of(firmwareFolder, version, NAME_DEFAULT_CONFIG));
            new ObjectMapper().readValue(config, ConfigPayload.class);
        } catch (Exception e) {
            throw new SensorApiException("Occurred problem with read default config", e);
        }

        return DeviceConfig
                .builder()
                .config(config)
                .checksum(checkSum(config + version))
                .forVersion(version)
                .time(OffsetDateTime.now())
                .device(device).build();

    }

    public void testConfigWithSchema(String jsonStr, String schemaStr) {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json;
        JsonNode jsonSchemaNode;
        try {
            json = objectMapper.readTree(jsonStr);
            jsonSchemaNode = objectMapper.readTree(schemaStr);
        } catch (JsonProcessingException e) {
            throw new SensorApiException(e, "Occurred problem with validate message");
        }

        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema schema = schemaFactory.getSchema(jsonSchemaNode);

        Set<ValidationMessage> validationResult = schema.validate(json);

        if (!validationResult.isEmpty()) {
            throw new SensorApiException("Occurred problem with validate message");
        }
    }

    public long checkSum(String checksum) {
        String check = checksum.replaceAll("\\s+", "");
        Checksum crc32 = new CRC32();
        crc32.update(check.getBytes(), 0, check.getBytes().length);
        return crc32.getValue();
    }

    public DeviceConfig currentDeviceConfigCheck(Device device) {
        return device.getCurrentDeviceConfig();
    }

    public ConfigPayload mapConfigToObject(String config) throws JsonProcessingException {
        return new ObjectMapper().readValue(config, ConfigPayload.class);
    }

}
