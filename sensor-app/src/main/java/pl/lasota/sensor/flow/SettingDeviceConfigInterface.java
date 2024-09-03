package pl.lasota.sensor.flow;

import pl.lasota.sensor.exceptions.SensorFlowException;

import java.util.List;


public interface SettingDeviceConfigInterface {

    List<Integer> configuredPwmGpioPins(String deviceId) throws SensorFlowException;

    List<Integer> configuredAnalogGpioPins(String deviceId) throws SensorFlowException;

    List<Integer> configuredDigitalGpioPins(String deviceId) throws SensorFlowException;


}
