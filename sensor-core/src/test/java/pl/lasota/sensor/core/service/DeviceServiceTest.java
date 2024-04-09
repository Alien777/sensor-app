package pl.lasota.sensor.core.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.lasota.sensor.core.configs.CoreProperties;
import pl.lasota.sensor.core.entities.device.Device;
import pl.lasota.sensor.core.exceptions.ConfigCheckSumExistException;
import pl.lasota.sensor.core.exceptions.ConfigParserException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceException;
import pl.lasota.sensor.core.exceptions.NotFoundSchemaConfigException;
import pl.lasota.sensor.core.repository.DeviceConfigRepository;
import pl.lasota.sensor.core.repository.DeviceRepository;
import pl.lasota.sensor.core.repository.MemberRepository;
import pl.lasota.sensor.core.repository.SensorRecordingRepository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

class DeviceServiceTest {

    static final String resourceDirectory = Paths.get("src", "test", "resources").toAbsolutePath().toString();
    

    @Test
    public void validate_config_test() throws ConfigCheckSumExistException, ConfigParserException, NotFoundSchemaConfigException, NotFoundDeviceException, IOException {
        Device device = Mockito.mock(Device.class);
        DeviceRepository drMock = Mockito.mock(DeviceRepository.class);
        CoreProperties cpMock = Mockito.mock(CoreProperties.class);
        SensorRecordingRepository srrMock = Mockito.mock(SensorRecordingRepository.class);
        DeviceConfigRepository dcrMock = Mockito.mock(DeviceConfigRepository.class);
        MemberRepository mrMock = Mockito.mock(MemberRepository.class);
        Mockito.when(cpMock.getFirmwareFolder()).thenReturn("/");
        Mockito.when(drMock.findDeviceBy(Mockito.same("1L"), Mockito.same("1L"))).thenReturn(Optional.of(device));
        DeviceService deviceService = new DeviceService(drMock, srrMock, dcrMock, mrMock, new DeviceUtilsService(cpMock),null);

        String config = "{\"analog_configs\": []}";
        validOk(deviceService, dcrMock, config);

        config = "{\"analog_configs\": [{\"pin\": 4, \"atten\": 3, \"width\": 12, \"min_adc\": 100, \"sampling\": 5000}]}";
        validOk(deviceService, dcrMock, config);

        config = "{\"analog_configs\": [{\"pin\": 4, \"atten\": 3}]}";
        validNoOk(deviceService, dcrMock, config);
    }

    private void validOk(DeviceService deviceService, DeviceConfigRepository dcrMock, String config) throws ConfigCheckSumExistException, ConfigParserException, NotFoundSchemaConfigException, NotFoundDeviceException {
        Mockito.reset(dcrMock);
        deviceService.saveConfig("1L", config, resourceDirectory, "1L");
        Mockito.verify(dcrMock, Mockito.times(1)).save(Mockito.any());
    }

    private void validNoOk(DeviceService deviceService, DeviceConfigRepository dcrMock, String config) {
        Mockito.reset(dcrMock);
        Assertions.assertThrowsExactly(ConfigParserException.class, () ->
                deviceService.saveConfig("1L", config, resourceDirectory, "1L"));

        Mockito.verify(dcrMock, Mockito.times(0)).save(Mockito.any());
    }
}