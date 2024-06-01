package pl.lasota.sensor.gateway.device.mqtt;

import org.eclipse.paho.mqttv5.common.MqttException;

public interface SimpleCallback {
      void handle() throws MqttException;
}
