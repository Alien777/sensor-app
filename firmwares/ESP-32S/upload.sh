#!/bin/sh

detect_esp_device() {
    python3 - <<END
import serial.tools.list_ports

ports = serial.tools.list_ports.comports()
for port in ports:
    if 'USB' in port.description or 'UART' in port.description:
        print(port.device)
END
}

echo "Detecting ESP device..."
DEVICE_PORT=$(detect_esp_device)

if [ -z "$DEVICE_PORT" ]; then
    echo "No ESP device detected. Please ensure the device is connected and try again."
    exit 1
else
    echo "ESP device detected on port: $DEVICE_PORT"
fi

if [ ! -f "./bootloader.bin" ]; then
    echo "Error: bootloader.bin not found!"
    exit 1
fi

if [ ! -f "./partitions.bin" ]; then
    echo "Error: partitions.bin not found!"
    exit 1
fi

if [ ! -f "./firmware.bin" ]; then
    echo "Error: firmware.bin not found!"
    exit 1
fi

if [ ! -f "./nvs.bin" ]; then
    echo "Error: nvs.bin not found!"
    exit 1
fi

if [ ! -f "./spiffs.bin" ]; then
    echo "Error: spiffs.bin not found!"
    exit 1
fi

if ! command -v esptool.py &> /dev/null; then
    echo "esptool.py not found, installing..."
    pip install esptool
fi

BOOTLOADER="./bootloader.bin"
PARTITION_TABLE="./partitions.bin"
FIRMWARE="./firmware.bin"
NVS="./nvs.bin"
SPIFFS="./spiffs.bin"

echo "Starting the firmware flashing process..."
esptool.py --chip esp32 --port "$DEVICE_PORT" --baud 921600 write_flash \
    0x1000 $BOOTLOADER \
    0x8000 $PARTITION_TABLE \
    0x9000 $NVS \
    0x20000 $FIRMWARE \
    0x320000 $SPIFFS

if [ $? -eq 0 ]; then
    echo "Firmware flashed successfully."
else
    echo "Failed to flash firmware."
    exit 1
fi

read -p "Naciśnij dowolny klawisz, aby zakończyć..."
