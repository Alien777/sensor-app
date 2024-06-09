@echo off

:detect_esp_device
for /f "delims=" %%i in ('python -c "import serial.tools.list_ports; ports = serial.tools.list_ports.comports(); print(next((port.device for port in ports if 'USB' in port.description or 'UART' in port.description), ''))"') do set DEVICE_PORT=%%i

echo Detecting ESP device...
call :detect_esp_device

if "%DEVICE_PORT%"=="" (
    echo No ESP device detected. Please ensure the device is connected and try again.
    pause
    exit /b 1
) else (
    echo ESP device detected on port: %DEVICE_PORT%
)

if not exist "bootloader.bin" (
    echo Error: bootloader.bin not found!
    exit /b 1
)

if not exist "partitions.bin" (
    echo Error: partitions.bin not found!
    exit /b 1
)

if not exist "firmware.bin" (
    echo Error: firmware.bin not found!
    exit /b 1
)

if not exist "nvs.bin" (
    echo Error: nvs.bin not found!
    exit /b 1
)

if not exist "spiffs.bin" (
    echo Error: spiffs.bin not found!
    exit /b 1
)

where esptool.py >nul 2>nul
if %errorlevel% neq 0 (
    echo esptool.py not found, installing...
    pip install esptool
)

set BOOTLOADER=bootloader.bin
set PARTITION_TABLE=partitions.bin
set FIRMWARE=firmware.bin
set NVS=nvs.bin
set SPIFFS=spiffs.bin

echo Starting the firmware flashing process...
esptool.py --chip esp32 --port %DEVICE_PORT% --baud 921600 write_flash ^
    0x1000 %BOOTLOADER% ^
    0x8000 %PARTITION_TABLE% ^
    0x9000 %NVS% ^
    0x20000 %FIRMWARE% ^
    0x320000 %SPIFFS%

if %errorlevel% equ 0 (
    echo Firmware flashed successfully.
) else (
    echo Failed to flash firmware.
    exit /b 1
)

pause
