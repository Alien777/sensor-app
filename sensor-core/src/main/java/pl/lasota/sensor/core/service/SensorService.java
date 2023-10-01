package pl.lasota.sensor.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.core.exceptions.NotFoundSensorConfigException;
import pl.lasota.sensor.core.model.*;
import pl.lasota.sensor.core.repository.MemberRepository;
import pl.lasota.sensor.core.repository.SensorConfigRepository;
import pl.lasota.sensor.core.repository.SensorRecordingRepository;
import pl.lasota.sensor.core.repository.SensorRepository;

import java.time.OffsetDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository sr;
    private final SensorRecordingRepository srr;
    private final SensorConfigRepository scr;
    private final MemberRepository mr;

    @Transactional
    public void insertReading(Message message, String value) {
        Optional<Sensor> sensorBy = sr.findSensorBy(message.getMemberKey(), message.getDeviceKey());
        sensorBy.ifPresent(sensor -> {
            SensorReading sensorReading = SensorReading.builder()
                    .messageType(message.getMessageType())
                    .message(value)
                    .time(OffsetDateTime.now())
                    .sensor(sensor)
                    .build();

            sensor.getSensorReading()
                    .add(sensorReading);

            srr.save(sensorReading);
            sr.save(sensor);
        });
    }

    @Transactional
    public void insertSensor(String memberKey, String deviceKey) {
        Optional<Member> memberByMemberKey = mr.findMemberByMemberKey(memberKey);
        memberByMemberKey.ifPresent(member -> {
            Sensor sensor = Sensor.builder()
                    .member(member)
                    .device_key(deviceKey)
                    .build();

            member.getSensors()
                    .add(sensor);

            sr.save(sensor);
            mr.save(member);
        });
    }

    public boolean isSensorExisting(String memberKey, String deviceKey) {
        return sr.existsSensor(memberKey, deviceKey);
    }

    public SensorConfig getLastSensorConfig(String deviceKey) throws NotFoundSensorConfigException {
        return scr.findLastSensorConfig(deviceKey).orElseThrow(NotFoundSensorConfigException::new);
    }

}
