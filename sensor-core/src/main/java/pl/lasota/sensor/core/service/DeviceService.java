package pl.lasota.sensor.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.core.exceptions.NotFoundSensorConfigException;
import pl.lasota.sensor.core.model.*;
import pl.lasota.sensor.core.model.device.Device;
import pl.lasota.sensor.core.model.device.DeviceConfig;
import pl.lasota.sensor.core.model.sensor.ConnectedDevice;
import pl.lasota.sensor.core.model.sensor.Sensor;
import pl.lasota.sensor.core.model.sensor.Sensor.SensorBuilder;
import pl.lasota.sensor.core.model.sensor.SingleAdcSignal;
import pl.lasota.sensor.core.mqttPayloads.MessageFrame;
import pl.lasota.sensor.core.repository.MemberRepository;
import pl.lasota.sensor.core.repository.DeviceConfigRepository;
import pl.lasota.sensor.core.repository.SensorRecordingRepository;
import pl.lasota.sensor.core.repository.DeviceRepository;

import java.time.OffsetDateTime;
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
                .device(device)
                .build();

        device.getSensor().add(sensor);

        srr.save(sensor);
        dr.save(device);

    }

    @Transactional
    public void insertDevice(String memberKey, String deviceKey) {
        Optional<Member> memberByMemberKey = mr.findMemberByMemberKey(memberKey);
        memberByMemberKey.ifPresent(member -> {
            Device device = Device.builder()
                    .member(member)
                    .deviceKey(deviceKey)
                    .build();

            member.getDevices()
                    .add(device);

            dr.save(device);
            mr.save(member);
        });
    }

    public boolean isDeviceExisting(String memberKey, String deviceKey) {
        return dr.existsSensor(memberKey, deviceKey);
    }

    public DeviceConfig getLastDeviceConfig(String deviceKey) throws NotFoundSensorConfigException {
        return dcr.findLastSensorConfig(deviceKey).orElseThrow(NotFoundSensorConfigException::new);
    }



}
