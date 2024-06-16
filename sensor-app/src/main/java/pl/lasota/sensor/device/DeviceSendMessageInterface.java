package pl.lasota.sensor.device;

import org.springframework.web.bind.annotation.RequestBody;
import pl.lasota.sensor.device.model.SendConfigI;
import pl.lasota.sensor.device.model.SendDigitalI;
import pl.lasota.sensor.device.model.SendForAnalogDataI;
import pl.lasota.sensor.device.model.SendPwmI;


public interface DeviceSendMessageInterface {

    void sendConfigToDevice(SendConfigI configS) throws Exception;

    void sendPwmValueToDevice(@RequestBody SendPwmI configS) throws Exception;

    void sendRequestForDataAnalog(@RequestBody SendForAnalogDataI configS) throws Exception;

    void sendDigitalValueToDevice(SendDigitalI configS) throws Exception;

    void sendPing(SendDigitalI configS) throws Exception;

}
