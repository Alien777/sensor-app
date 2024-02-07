# SENSOR PROJECT
## Why?
### I want to create simple system to remote manage of sensors. Project could be to use for Smart Home and others

## Structure of project
### esp-firmware:
#### Folder contain firmware for ESP32. Firmware is has been written in C Language.
### sensor-core
#### ---
### sensor-api
#### layer provide communicate between esp <-> mqtt <-> sensor-api <-> database 
### sensor-gui
#### layer for user, to setup sensor config etc.

## Flow:


* EPS -> MQTT-> SENSOR APP - connection device
* SENSOR APP -> MQTT -> ESP - sending pin config
* ESP -> MQTT ->  SENSOR APP - analog adc value sending


## Payload configuration from server to ESP. sensor_runner.c

```json
{
  "output": [
    {
      "pin": 4,
      "type": "ANALOG",
      "atten": 3,
      "width": 12,
      "sampling": 5000,  //sleep thread
      "min_adc": 100, //barrier min- after across sensor value won't send to server
      "max_adc": 2000 //barrier max- after across sensor value won't send to server
    }
  ]
}
```
also send device_key, member_key.

## Esp panel
![img.png](readme/esp-panel.png)

## Gui panel
![img.png](readme/gui-panel.png)

## TODO
### The list below will be updated
* ~~Simple layout for ESP~~
* ~~WIFI manager. If doesn't connect to WiFi then ESP is mode AP otherwise ESP is mode STA~~
* ~~Created simple structure of code~~
* Encrypted communicate between mqtt and clients (I will share config of mqtt)
* Mapping config json to instruction C for reading value in sensor. User will write "please read value in pin 23..." and next this command will be sent to ESP.
  * Analog,
  * Digital
* Dynamic OTA firmware update
* Tests


### JAVA, C, MQTT, POSTGRES, NUXT
