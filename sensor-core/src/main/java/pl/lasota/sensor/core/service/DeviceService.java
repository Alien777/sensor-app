package pl.lasota.sensor.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.core.exceptions.*;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.device.Device;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.models.mqtt.payload.to.ConfigPayload;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.repository.DeviceConfigRepository;
import pl.lasota.sensor.core.repository.DeviceRepository;
import pl.lasota.sensor.core.repository.MemberRepository;
import pl.lasota.sensor.core.repository.SensorRecordingRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static pl.lasota.sensor.core.models.mqtt.payload.MessageType.DEVICE_CONNECTED;


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
        Optional<Device> deviceOptional = dr.findDeviceBy(messageFrame.getMemberKey(), messageFrame.getDeviceKey());

        if (deviceOptional.isEmpty()) {
            return null;
        }
        Device device = deviceOptional.get();

        var sensorBuilder = messageFrame.getPayloadFromDriver();
        if (sensorBuilder == null) {
            return null;
        }
        Optional<DeviceConfig> configOptional = dcr.getDeviceConfig(device.getDeviceKey(), messageFrame.getConfigIdentifier());

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

    @Transactional(rollbackFor = SensorException.class)
    public void insertNewDevice(String memberKey, String deviceKey, String version) throws NotFoundDefaultConfigException, NotFoundMemberException {
        if (dr.existsDevice(memberKey, deviceKey)) {
            return;
        }

        Member member = mr.findMemberByMemberKey(memberKey).orElseThrow(NotFoundMemberException::new);

        Device device = Device.builder()
                .member(member)
                .version(version)
                .deviceKey(deviceKey)
                .build();

        member.getDevices()
                .add(device);

        DeviceConfig deviceConfig = dsu.createDefaultDeviceConfig(version, device);
        device.setCurrentDeviceConfig(deviceConfig);

        dr.save(device);
        mr.save(member);

    }

    @Transactional
    public DeviceConfig currentDeviceConfig(String memberKey, String deviceKey) throws NotFoundDeviceException, NotFoundDeviceConfigException {
        Device device = dr.findDeviceBy(memberKey, deviceKey).orElseThrow(NotFoundDeviceException::new);
        return dsu.currentDeviceConfigCheck(device);
    }

    @Transactional
    public DeviceConfig currentDeviceConfig(Long memberId, Long deviceId) throws NotFoundDeviceException, NotFoundDeviceConfigException {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(NotFoundDeviceException::new);
        return dsu.currentDeviceConfigCheck(device);
    }

    @Transactional
    public List<DeviceConfig> getConfigForDevice(Long memberId, Long deviceId) throws NotFoundDeviceException {
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
    public DeviceConfig saveConfig(Long memberId, String config, String versionConfig, Long deviceId) throws NotFoundDeviceException, NotFoundSchemaConfigException, ConfigParserException, ConfigCheckSumExistException {
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
    public void activateConfig(Long memberId, Long deviceId, Long configId) throws NotFoundDeviceException, NotFoundDefaultConfigException, NotFoundSchemaConfigException, ConfigParserException, JsonProcessingException {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(NotFoundDeviceException::new);
        DeviceConfig deviceConfig = dcr.getDeviceConfig(deviceId, configId).orElseThrow(NotFoundDefaultConfigException::new);
        dsu.testConfigWithSchema(deviceConfig.getConfig(), dsu.schemaForVersion(device.getVersion()));
        device.setCurrentDeviceConfig(deviceConfig);
        dr.save(device);
    }

    public String getDeviceKey(Long memberId, Long deviceId) throws NotFoundDeviceException {
        return dr.findDeviceBy(memberId, deviceId).orElseThrow(NotFoundDeviceException::new).getDeviceKey();
    }

    public boolean isDeviceExist(String memberKey, String deviceKey) {
        return dr.existsDevice(memberKey, deviceKey);
    }

    public List<Device> getAllDeviceBy(Long id) {
        return dr.findAllDevicesBy(id);
    }

    public boolean hasConfig(Long deviceId) {
        return dcr.existsDeviceConfigBy(deviceId);
    }


}
