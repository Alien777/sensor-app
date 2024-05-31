package pl.lasota.sensor.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.api.entities.*;
import pl.lasota.sensor.api.exception.SensorApiException;
import pl.lasota.sensor.api.payload.MessageFrame;
import pl.lasota.sensor.api.payload.MessageType;
import pl.lasota.sensor.api.payload.to.AnalogConfig;
import pl.lasota.sensor.api.payload.to.ConfigPayload;
import pl.lasota.sensor.api.payload.to.PwmConfig;
import pl.lasota.sensor.api.repositories.DeviceConfigRepository;
import pl.lasota.sensor.api.repositories.DeviceRepository;
import pl.lasota.sensor.api.repositories.DeviceTemporaryRepository;
import pl.lasota.sensor.api.repositories.SensorRecordingRepository;
import pl.lasota.sensor.core.exceptions.SensorException;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pl.lasota.sensor.api.payload.MessageType.DEVICE_CONNECTED;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository dr;
    private final SensorRecordingRepository srr;
    private final DeviceConfigRepository dcr;
    private final DeviceUtilsService dsu;
    private final DeviceTemporaryRepository dtr;

    @Transactional(rollbackFor = SensorException.class)
    public Sensor insertSensorReading(MessageFrame messageFrame) {
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
            throw new SensorApiException("Not found device config");
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
    public String saveTemporary(String memberId, String deviceId, String name) {
        String token = UUID.randomUUID().toString();
        DeviceToken deviceToken = DeviceToken.builder()
                .member(memberId)
                .token(token).build();

        dtr.save(DeviceTemporary.builder()
                .time(OffsetDateTime.now())
                .name(name)
                .device(deviceId)
                .member(memberId)
                .currentDeviceToken(deviceToken).build());
        return token;

    }


    @Transactional
    public boolean moveToDeviceFromTemporary(String memberId, String deviceId, String token) {
        Optional<DeviceTemporary> tokenValid = dtr.isTokenValid(memberId, token);
        if (tokenValid.isEmpty()) {
            return false;
        }
        DeviceTemporary deviceTemporary = tokenValid.get();
        Device device = Device.builder()
                .member(memberId)
                .name(deviceTemporary.getName())
                .currentDeviceToken(deviceTemporary.getCurrentDeviceToken())
                .id(deviceId.toUpperCase())
                .build();
        dr.save(device);
        dtr.delete(deviceTemporary);
        return true;
    }

    @Transactional(rollbackFor = SensorException.class)
    public void setUpVersion(String memberId, String deviceId, String version) {
        if (!dr.existsDevice(memberId, deviceId)) {
            return;
        }
        Device device = dr.getReferenceById(deviceId);
        if (device.getVersion() != null) {
            return;
        }

        device.setVersion(version);

        DeviceConfig deviceConfig = dsu.createDefaultDeviceConfig(version, device);
        device.setCurrentDeviceConfig(deviceConfig);

        dr.save(device);
    }

    @Transactional
    public DeviceConfig currentDeviceConfig(String memberId, String deviceId) {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(() -> new SensorApiException("Not found device by {}", deviceId));
        return dsu.currentDeviceConfigCheck(device);
    }

    @Transactional
    public LinkedList<DeviceConfig> getConfigForDevice(String memberId, String deviceId) {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(() -> new SensorApiException("Not found device by {}", deviceId));
        LinkedList<DeviceConfig> allConfigBy = dcr.findAllDeviceConfigBy(device.getId());
        if (device.getCurrentDeviceConfig() == null) {
            return allConfigBy;
        }
        allConfigBy.remove(device.getCurrentDeviceConfig());
        allConfigBy.addFirst(device.getCurrentDeviceConfig());
        return allConfigBy;
    }


    @Transactional
    public DeviceConfig saveConfig(String memberId, String config, String versionConfig, String deviceId) {
        try {
            new ObjectMapper().readValue(config, ConfigPayload.class);
        } catch (JsonProcessingException e) {
            throw new SensorApiException(e, "Occurred problem with parser config");
        }


        dsu.testConfigWithSchema(config, dsu.schemaForVersion(versionConfig));

        Device deviceOptional = dr.findDeviceBy(memberId, deviceId).orElseThrow(() -> new SensorApiException("Not found device by {}", deviceId));

        long checkSum = dsu.checkSum(config + versionConfig);
        Optional<DeviceConfig> existByCheckSum = dcr.getConfigByChecksum(checkSum, deviceOptional.getId());

        if (existByCheckSum.isPresent()) {
            throw new SensorApiException("All ready exist configuration " + existByCheckSum.get().getId());
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
    public void activateConfig(String memberId, String deviceId, Long configId) {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(() -> new SensorApiException("Not found device by {}", deviceId));
        DeviceConfig deviceConfig = dcr.getDeviceConfig(deviceId, configId).orElseThrow(() -> new SensorApiException("Not found device config by {}", deviceId));
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

    public List<DeviceTemporary> getAllTemporaryBy(String memberId) {
        return dtr.findAllDevicesBy(memberId);
    }


    public boolean hasConfig(String deviceId) {
        return dcr.existsDeviceConfigBy(deviceId);
    }


    public Optional<Device> getDevice(String memberId, String deviceId) {
        return dcr.getDevice(memberId, deviceId);
    }


    public DeviceConfig getConfig(String memberId, String deviceId, String configId) {
        return dcr.getDeviceConfigById(memberId, deviceId, configId);
    }

    @Transactional
    public List<Integer> getPwmPins(String memberId, String deviceId) {

        DeviceConfig deviceConfig = currentDeviceConfig(memberId, deviceId);
        ConfigPayload configPayload;
        try {
            configPayload = dsu.mapConfigToObject(deviceConfig.getConfig());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return configPayload.getPwmConfig().stream().map(PwmConfig::getPin).collect(Collectors.toList());
    }

    @Transactional
    public List<Integer> getAnalogPins(String memberId, String deviceId) {

        DeviceConfig deviceConfig = currentDeviceConfig(memberId, deviceId);
        ConfigPayload configPayload;
        try {
            configPayload = dsu.mapConfigToObject(deviceConfig.getConfig());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return configPayload.getAnalogReader().stream().map(AnalogConfig::getPin).collect(Collectors.toList());
    }

    @Transactional
    public List<MessageType> getMessageType(String memberId, String deviceId) {
        ConfigPayload configPayload;
        DeviceConfig deviceConfig = currentDeviceConfig(memberId, deviceId);
        try {
            configPayload = dsu.mapConfigToObject(deviceConfig.getConfig());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (!configPayload.getAnalogReader().isEmpty()) {
            return MessageType.getListMessageTypeFromDevice();
        }

        return Collections.singletonList(MessageType.DEVICE_CONNECTED);
    }
}
