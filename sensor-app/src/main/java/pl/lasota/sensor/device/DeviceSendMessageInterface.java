package pl.lasota.sensor.device;

import pl.lasota.sensor.device.model.*;

import java.util.UUID;


public interface DeviceSendMessageInterface {

    UUID sendConfig(ConfigMessage configS) throws Exception;

    UUID sendPwmWriteRequest(PwmWriteRequestMessage pwmWriteRequestMessage) throws Exception;

    UUID sendAnalogReadOneShotRequest(AnalogReadOneShotRequestMessage analogReadOneShotRequestMessage) throws Exception;

    UUID sendDigitalWriteRequest(DigitalWriteMessage digitalWriteMessage) throws Exception;

    UUID sendPwmWriteSetUp(PwmWriteSetUpMessage pwmWriteSetUpMessage) throws Exception;

    UUID sendAnalogReadSetUp(AnalogReadSetUpMessage analogReadSetUpMessage) throws Exception;

    UUID sendDigitalSetUp(DigitalSetUpMessage messageDigitalSetUp) throws Exception;

    UUID sendPing(PingMessage pingMessage) throws Exception;
}
