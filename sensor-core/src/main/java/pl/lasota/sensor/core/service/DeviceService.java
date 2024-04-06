package pl.lasota.sensor.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.core.entities.DeviceToken;
import pl.lasota.sensor.core.exceptions.*;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.entities.device.Device;
import pl.lasota.sensor.core.entities.device.DeviceConfig;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.entities.mqtt.payload.to.ConfigPayload;
import pl.lasota.sensor.core.entities.sensor.Sensor;
import pl.lasota.sensor.core.repository.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static pl.lasota.sensor.core.entities.mqtt.payload.MessageType.DEVICE_CONNECTED;


@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository dr;
    private final SensorRecordingRepository srr;
    private final DeviceConfigRepository dcr;
    private final MemberRepository mr;
    private final DeviceUtilsService dsu;

    @Transactional(rollbackFor = SensorException.class)
    public Sensor insertSensorReading(MessageFrame messageFrame) throws NotFoundDefaultConfigException {
        Optional<Device> deviceOptional = dr.findDeviceBy(messageFrame.getMemberId(), messageFrame.getDeviceId());

        if (deviceOptional.isEmpty()) {
            return null;
        }
        Device device = deviceOptional.get();

        var sensorBuilder = messageFrame.getPayloadFromDriver();
        if (sensorBuilder == null) {
            return null;
        }
        Optional<DeviceConfig> configOptional = dcr.getDeviceConfig(device.getId(), messageFrame.getConfigIdentifier());

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

        return sensor;

    }

    @Transactional
    public String save(String memberId, String deviceId, String name) {
        String token = UUID.randomUUID().toString();
        DeviceToken deviceToken = DeviceToken.builder().member(Member.builder().id(memberId).build())
                .token(token).build();

        Device device = Device.builder()
                .member(Member.builder().id(memberId).build())
                .name(name)
                .currentDeviceToken(deviceToken)
                .id(deviceId.toUpperCase())
                .build();
        dr.save(device);
        return token;
    }

    @Transactional(rollbackFor = SensorException.class)
    public void setUpVersion(String memberId, String deviceId, String version) throws NotFoundDefaultConfigException, NotFoundMemberException {
        if (!dr.existsDevice(memberId, deviceId)) {
            return;
        }
        Device device = dr.getReferenceById(deviceId);
        if (device.getVersion() != null) {
            return;
        }

        Member member = mr.findMemberById(memberId).orElseThrow(NotFoundMemberException::new);
        device.setVersion(version);;

        DeviceConfig deviceConfig = dsu.createDefaultDeviceConfig(version, device);
        device.setCurrentDeviceConfig(deviceConfig);

        dr.save(device);
        mr.save(member);

    }

    @Transactional
    public DeviceConfig currentDeviceConfig(String memberId, String deviceId) throws NotFoundDeviceException {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(NotFoundDeviceException::new);
        return dsu.currentDeviceConfigCheck(device);
    }

    @Transactional
    public List<DeviceConfig> getConfigForDevice(String memberId, String deviceId) throws NotFoundDeviceException {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(NotFoundDeviceException::new);
        List<DeviceConfig> allConfigBy = dcr.findAllDeviceConfigBy(device.getId());
        if (device.getCurrentDeviceConfig() == null) {
            return allConfigBy;
        }
        allConfigBy.remove(device.getCurrentDeviceConfig());
        allConfigBy.addFirst(device.getCurrentDeviceConfig());
        return allConfigBy;
    }


    @Transactional
    public DeviceConfig saveConfig(String memberId, String config, String versionConfig, String deviceId) throws NotFoundDeviceException, NotFoundSchemaConfigException, ConfigParserException, ConfigCheckSumExistException {
        try {
            new ObjectMapper().readValue(config, ConfigPayload.class);
        } catch (JsonProcessingException e) {
            throw new ConfigParserException(e);
        }

        try {
            dsu.testConfigWithSchema(config, dsu.schemaForVersion(versionConfig));
        } catch (JsonProcessingException e) {
            throw new ConfigParserException(e);
        }

        Device deviceOptional = dr.findDeviceBy(memberId, deviceId).orElseThrow(NotFoundDeviceException::new);

        long checkSum = dsu.checkSum(config + versionConfig);
        Optional<DeviceConfig> existByCheckSum = dcr.getConfigByChecksum(checkSum);

        if (existByCheckSum.isPresent()) {
            throw new ConfigCheckSumExistException("All ready exist configuration " + existByCheckSum.get().getId());
        }

        DeviceConfig build = DeviceConfig
                .builder()
                .device(deviceOptional)
                .time(OffsetDateTime.now())
                .forVersion(versionConfig)
                .config(config)
                .checksum(checkSum)
                .build();

        return dcr.save(build);
    }

    @Transactional
    public void activateConfig(String memberId, String deviceId, Long configId) throws NotFoundDeviceException, NotFoundDefaultConfigException, NotFoundSchemaConfigException, ConfigParserException, JsonProcessingException {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(NotFoundDeviceException::new);
        DeviceConfig deviceConfig = dcr.getDeviceConfig(deviceId, configId).orElseThrow(NotFoundDefaultConfigException::new);
        dsu.testConfigWithSchema(deviceConfig.getConfig(), dsu.schemaForVersion(device.getVersion()));
        device.setCurrentDeviceConfig(deviceConfig);
        dr.save(device);
    }


    public boolean isDeviceExist(String memberId, String deviceId) {
        return dr.existsDevice(memberId, deviceId);
    }

    public boolean isTokenValid(String memberId, String deviceId, String token) {
        return dr.isTokenValid(memberId, deviceId, token);
    }

    public List<Device> getAllDeviceBy(String memberId) {
        return dr.findAllDevicesBy(memberId);
    }

    public boolean hasConfig(String deviceId) {
        return dcr.existsDeviceConfigBy(deviceId);
    }


    public Optional<Device> getDevice(String memberId, String deviceId) {
        return dcr.getDevice(memberId, deviceId);
    }


}
