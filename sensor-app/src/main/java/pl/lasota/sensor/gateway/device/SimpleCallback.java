package pl.lasota.sensor.gateway.device;

import org.eclipse.paho.mqttv5.common.MqttException;

public interface SimpleCallback {
      void handle() throws MqttException;
}
