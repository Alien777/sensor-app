package pl.lasota.sensor.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.core.exceptions.NotFoundSensorConfigException;
import pl.lasota.sensor.core.models.*;
import pl.lasota.sensor.core.models.device.Device;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.models.sensor.ConnectedDevice;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.models.sensor.Sensor.SensorBuilder;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.repository.MemberRepository;
import pl.lasota.sensor.core.repository.DeviceConfigRepository;
import pl.lasota.sensor.core.repository.SensorRecordingRepository;
import pl.lasota.sensor.core.repository.DeviceRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository dr;
    private final SensorRecordingRepository srr;
    private final DeviceConfigRepository dcr;
    private final MemberRepository mr;

    @Transactional
    public void insertReading(MessageFrame messageFrame) throws JsonProcessingException {
        Optional<Device> deviceOptional = dr.findSensorBy(messageFrame.getMemberKey(), messageFrame.getDeviceKey());

        if (deviceOptional.isEmpty()) {
            return;
        }
        Device device = deviceOptional.get();

        SensorBuilder sensorBuilder = switch (messageFrame.getMessageType()) {
            case DEVICE_CONNECTED -> ConnectedDevice.builder();
            case CONFIG -> null;
            case SINGLE_ADC_SIGNAL -> messageFrame.getSingleAdcSignal();
        };

        if (sensorBuilder == null) {
            return;
        }

        Sensor sensor = sensorBuilder.time(OffsetDateTime.now())
                .device(device)
                .messageType(messageFrame.getMessageType())
                .build();

        device.getSensor().add(sensor);

        srr.save(sensor);
        dr.save(device);

    }

    @Transactional
    public void insertDevice(String memberKey, String deviceKey, String version) {
        Optional<Member> memberByMemberKey = mr.findMemberByMemberKey(memberKey);
        memberByMemberKey.ifPresent(member -> {
            Device device = Device.builder()
                    .member(member)
                    .version(version)
                    .version(deviceKey)
                    .deviceKey(deviceKey)
                    .build();

            member.getDevices()
                    .add(device);

            dr.save(device);
            mr.save(member);
        });
    }

    @Transactional
    public boolean updateVersion(String memberKey, String deviceKey, String version) {
        if (version == null || version.isEmpty()) {
            return false;
        }
        Optional<Device> deviceOptional = dr.findSensorBy(memberKey, deviceKey);
        if (deviceOptional.isEmpty()) {
            return false;
        }

        Device device = deviceOptional.get();
        if (version.equals(device.getVersion())) {
            return false;
        }

        device.setVersion(version);
        return true;
    }

    public boolean isDeviceExisting(String memberKey, String deviceKey) {
        return dr.existsSensor(memberKey, deviceKey);
    }

    public DeviceConfig getLastDeviceConfig(String deviceKey, String version) throws NotFoundSensorConfigException {
        return dcr.findLastSensorConfig(deviceKey, version).orElseThrow(NotFoundSensorConfigException::new);
    }

    public List<Device> getAllDevice(String memberKey) {
        return dr.findAllForMember(memberKey);
    }
}
