package pl.lasota.sensor.core.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.core.exceptions.*;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.device.Device;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.models.sensor.ConnectedDevice;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.repository.DeviceConfigRepository;
import pl.lasota.sensor.core.repository.DeviceRepository;
import pl.lasota.sensor.core.repository.MemberRepository;
import pl.lasota.sensor.core.repository.SensorRecordingRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static pl.lasota.sensor.core.models.MessageType.DEVICE_CONNECTED;


@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {
    private final static String NAME_DEFAULT_CONFIG = "defaultConfig.json";
    private final static String NAME_SCHEMA_CONFIG = "schema.json";
    private final DeviceRepository dr;
    private final SensorRecordingRepository srr;
    private final DeviceConfigRepository dcr;
    private final MemberRepository mr;

    @Value("${sensor.core.firmware}")
    private String firmwareFolder;


    @Transactional(rollbackFor = SensorException.class)
    public void insertSensorReading(MessageFrame messageFrame) throws NotFoundDefaultConfigException {
        Optional<Device> deviceOptional = dr.findDeviceBy(messageFrame.getMemberKey(), messageFrame.getDeviceKey());

        if (deviceOptional.isEmpty()) {
            return;
        }
        Device device = deviceOptional.get();

        var sensorBuilder = switch (messageFrame.getMessageType()) {
            case DEVICE_CONNECTED -> ConnectedDevice.builder();
            case CONFIG -> null;
            case SINGLE_ADC_SIGNAL -> messageFrame.getSingleAdcSignal();
        };

        if (sensorBuilder == null) {
            return;
        }
        Optional<DeviceConfig> configOptional = dcr.getDevice(device.getDeviceKey(), messageFrame.getConfigId());

        if (configOptional.isEmpty() && !DEVICE_CONNECTED.equals(messageFrame.getMessageType())) {
            throw new NotFoundDefaultConfigException();
        }

        Sensor sensor = sensorBuilder.time(OffsetDateTime.now())
                .device(device)
                .forConfig(configOptional.orElse(device.getCurrentDeviceConfig()))
                .messageType(messageFrame.getMessageType())
                .build();

        device.getSensor().add(sensor);

        srr.save(sensor);
        dr.save(device);

    }

    @Transactional(rollbackFor = SensorException.class)
    public void insertNewDevice(String memberKey, String deviceKey, String version) throws NotFoundDefaultConfigException {
        if (dr.existsDevice(memberKey, deviceKey)) {
            return;
        }

        Optional<Member> optionalMember = mr.findMemberByMemberKey(memberKey);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            Device device = Device.builder()
                    .member(member)
                    .version(version)
                    .deviceKey(deviceKey)
                    .build();

            member.getDevices()
                    .add(device);


            DeviceConfig deviceConfig = createDeviceConfig(version, device);
            if (deviceConfig != null) {
                device.setCurrentDeviceConfig(deviceConfig);
            }

            dr.save(device);
            mr.save(member);

        }
    }

    @Transactional
    public DeviceConfig currentDeviceConfig(String deviceKey, String memberKey) throws NotFoundDeviceException, NotFoundDeviceConfigException {
        return currentDeviceConfig(dr.findDeviceBy(memberKey, deviceKey).orElseThrow(NotFoundDeviceException::new).getId());
    }

    @Transactional
    public DeviceConfig currentDeviceConfig(Long deviceId) throws NotFoundDeviceException, NotFoundDeviceConfigException {
        Device device = dr.findById(deviceId).orElseThrow(NotFoundDeviceException::new);
        DeviceConfig currentDeviceConfig = device.getCurrentDeviceConfig();
        if (currentDeviceConfig == null) {
            throw new NotFoundDeviceConfigException();
        }
        return currentDeviceConfig;
    }

    @Transactional
    public List<DeviceConfig> getConfigForDevice(Long deviceId) throws NotFoundDeviceException {
        Device device = dr.findById(deviceId).orElseThrow(NotFoundDeviceException::new);
        List<DeviceConfig> allConfigBy = dcr.findAllConfigBy(device.getId());
        //Always want to first config as activated
        if (device.getCurrentDeviceConfig() == null) {
            return allConfigBy;
        }
        allConfigBy.remove(device.getCurrentDeviceConfig());
        allConfigBy.addFirst(device.getCurrentDeviceConfig());
        return allConfigBy;
    }


    @Transactional
    public DeviceConfig saveConfig(String config, String versionConfig, Long deviceId) throws NotFoundDeviceException, NotFoundSchemaConfigException, ConfigParserException {
        try {
            testConfigWithSchema(config, schemaForVersion(versionConfig));
        } catch (JsonProcessingException e) {
            throw new ConfigParserException(e);
        }

        Device deviceOptional = dr.findById(deviceId).orElseThrow(NotFoundDeviceException::new);
        DeviceConfig build = DeviceConfig
                .builder()
                .device(deviceOptional)
                .time(OffsetDateTime.now())
                .forVersion(versionConfig)
                .config(config).build();
        return dcr.save(build);
    }

    public List<Device> getAllDeviceBy(String memberKey) {
        return dr.findAllDevicesBy(memberKey);
    }

    public boolean hasConfig(Long deviceId) {
        return dcr.existsDeviceConfigBy(deviceId);
    }


    public String schemaForVersion(String schemaVersion) throws NotFoundSchemaConfigException {

        try {
            return Files.readString(Path.of(firmwareFolder, schemaVersion, NAME_SCHEMA_CONFIG));
        } catch (Exception e) {
            throw new NotFoundSchemaConfigException(e);
        }

    }

    private DeviceConfig createDeviceConfig(String version, Device device) throws NotFoundDefaultConfigException {

        if (device.getCurrentDeviceConfig() != null) {
            return null;
        }
        String defaultConfig;

        try {
            defaultConfig = Files.readString(Path.of(firmwareFolder, version, NAME_DEFAULT_CONFIG));
        } catch (Exception e) {
            throw new NotFoundDefaultConfigException(e);
        }

        if (defaultConfig == null) {
            throw new NotFoundDefaultConfigException();
        }

        return DeviceConfig
                .builder()
                .config(defaultConfig)
                .forVersion(version)
                .time(OffsetDateTime.now())
                .device(device).build();

    }

    private void testConfigWithSchema(String jsonStr, String schemaStr) throws JsonParseException, JsonProcessingException, ConfigParserException {

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

}
