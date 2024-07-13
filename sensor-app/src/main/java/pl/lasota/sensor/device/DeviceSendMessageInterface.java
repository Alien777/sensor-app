package pl.lasota.sensor.device;

import org.springframework.web.bind.annotation.RequestBody;
import pl.lasota.sensor.device.model.SendConfigI;
import pl.lasota.sensor.device.model.SendDigitalI;
import pl.lasota.sensor.device.model.SendForAnalogDataI;
import pl.lasota.sensor.device.model.SendPwmI;

import java.util.UUID;


public interface DeviceSendMessageInterface {

    UUID sendConfigToDevice(SendConfigI configS) throws Exception;

    UUID sendPwmValueToDevice(@RequestBody SendPwmI configS) throws Exception;

    UUID sendRequestForDataAnalog(@RequestBody SendForAnalogDataI configS) throws Exception;

    UUID sendDigitalValueToDevice(SendDigitalI configS) throws Exception;

    UUID sendPing(SendDigitalI configS) throws Exception;

}
