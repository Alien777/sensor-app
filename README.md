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
