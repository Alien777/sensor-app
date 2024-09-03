package pl.lasota.sensor.device.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.entities.Device;
import pl.lasota.sensor.exceptions.SensorApiException;
import pl.lasota.sensor.gateway.device.MqttMessagePublish;
import pl.lasota.sensor.payload.MessageFrame;
import pl.lasota.sensor.payload.PayloadParser;
import pl.lasota.sensor.payload.PayloadType;
import pl.lasota.sensor.payload.message.Config;
import pl.lasota.sensor.payload.message.Ping;
import pl.lasota.sensor.payload.message.analog.AnalogReadOneShotRequest;
import pl.lasota.sensor.payload.message.analog.AnalogReadSetUp;
import pl.lasota.sensor.payload.message.digital.DigitalSetUp;
import pl.lasota.sensor.payload.message.digital.DigitalWriteRequest;
import pl.lasota.sensor.payload.message.pwm.PwmWriteRequest;
import pl.lasota.sensor.payload.message.pwm.PwmWriteSetUp;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceMessagePublish {

    private final MqttMessagePublish mqttMessagePublish;
    private final DeviceDataService deviceDataService;

    public UUID sendConfig(String memberId, String deviceId) throws Exception {
        MessageFrame messageFrame = build(memberId, deviceId, PayloadType.CONFIG, Config::of);
        mqttMessagePublish.publish(messageFrame);
        return messageFrame.getRequestId();
    }

    public UUID sendDigitalWriteRequest(String memberId, String deviceId, int gpio, boolean level) throws Exception {
        MessageFrame messageFrame = build(memberId, deviceId, PayloadType.DIGITAL_WRITE_REQUEST, () -> DigitalWriteRequest.of(gpio, level ? 1 : 0));
        mqttMessagePublish.publish(messageFrame);
        return messageFrame.getRequestId();
    }

    public UUID sendPwmWriteRequest(String memberId, String deviceId, int gpio, long duty, long duration) throws Exception {
        MessageFrame messageFrame = build(memberId, deviceId, PayloadType.PWM_WRITE_REQUEST, () -> PwmWriteRequest.of(gpio, duty, duration));
        mqttMessagePublish.publish(messageFrame);
        return messageFrame.getRequestId();
    }

    public UUID sendAnalogReadOneShotRequest(String memberId, String deviceId, int gpio) throws Exception {
        MessageFrame messageFrame = build(memberId, deviceId, PayloadType.ANALOG_READ_ONE_SHOT_REQUEST, () -> AnalogReadOneShotRequest.of(gpio));
        mqttMessagePublish.publish(messageFrame);
        return messageFrame.getRequestId();
    }

    public UUID sendPing(String memberId, String deviceId) throws Exception {
        MessageFrame messageFrame = build(memberId, deviceId, PayloadType.PING, Ping::of);
        mqttMessagePublish.publish(messageFrame);
        return messageFrame.getRequestId();
    }

    public UUID sendPwmWriteSetUp(String memberId, String deviceId, int gpio, int frequency, int resolution, int duty) throws Exception {
        MessageFrame messageFrame = build(memberId, deviceId, PayloadType.PWM_WRITE_SET_UP, () -> PwmWriteSetUp.of(gpio, frequency, resolution, duty));
        mqttMessagePublish.publish(messageFrame);
        return messageFrame.getRequestId();
    }


    public UUID sendDigitalSetUp(String memberId, String deviceId, int gpio, int mode) throws Exception {
        MessageFrame messageFrame = build(memberId, deviceId, PayloadType.DIGITAL_SET_UP, () -> DigitalSetUp.of(gpio, mode));
        mqttMessagePublish.publish(messageFrame);
        return messageFrame.getRequestId();
    }

    public UUID sendAnalogReadSetUp(String memberId, String deviceId, int gpio, int resolution) throws Exception {
        MessageFrame messageFrame = build(memberId, deviceId, PayloadType.ANALOG_READ_SET_UP, () -> AnalogReadSetUp.of(gpio, resolution));
        mqttMessagePublish.publish(messageFrame);
        return messageFrame.getRequestId();
    }

    public MessageFrame build(String memberId, String deviceId, PayloadType payloadType, Supplier<PayloadParser<?, String>> supplier) throws Exception {
        Optional<Device> deviceOptional = deviceDataService.getDevice(memberId, deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            return MessageFrame.of(memberId, deviceId, device.getCurrentDeviceToken().getToken(), device.getVersion(), payloadType, supplier.get());
        }
        throw new SensorApiException("Problem with send  message because device do not exist {}, {}", memberId, deviceId);
    }

}
