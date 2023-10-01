package pl.lasota.sensor.api.mqtt;

import org.eclipse.paho.mqttv5.common.MqttException;

public interface SimpleCallback {
      void handle() throws MqttException;
}
