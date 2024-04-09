package pl.lasota.sensor.core.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.lasota.sensor.core.configs.CoreProperties;
import pl.lasota.sensor.core.exceptions.*;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.entities.device.Device;
import pl.lasota.sensor.core.entities.device.DeviceConfig;
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
    void not_create_device_if_exist_test() throws NotFoundDefaultConfigException, NotFoundMemberException {

        DeviceRepository drMock = Mockito.mock(DeviceRepository.class);
        SensorRecordingRepository srrMock = Mockito.mock(SensorRecordingRepository.class);
        DeviceConfigRepository dcMock = Mockito.mock(DeviceConfigRepository.class);
        MemberRepository mrMock = Mockito.mock(MemberRepository.class);
        DeviceUtilsService dsuMock = Mockito.mock(DeviceUtilsService.class);

        Mockito.when(drMock.existsDevice(Mockito.same("0123456789123456"), Mockito.same("012345678912"))).thenReturn(true);

        DeviceService deviceService = new DeviceService(drMock, srrMock, dcMock, mrMock, dsuMock,null);
        deviceService.setUpVersion("0123456789123456", "012345678912", "1.0");

        Mockito.verify(drMock, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void create_device_if_not_exist_test() throws NotFoundDefaultConfigException, NotFoundMemberException {
        Member member = Mockito.mock(Member.class);
        DeviceRepository drMock = Mockito.mock(DeviceRepository.class);
        SensorRecordingRepository srrMock = Mockito.mock(SensorRecordingRepository.class);
        DeviceConfigRepository dcMock = Mockito.mock(DeviceConfigRepository.class);
        MemberRepository mrMock = Mockito.mock(MemberRepository.class);
        DeviceUtilsService dsuMock = Mockito.mock(DeviceUtilsService.class);

        Mockito.when(drMock.existsDevice(Mockito.same("0123456789123456"), Mockito.same("012345678912"))).thenReturn(false);
        Mockito.when(mrMock.findMemberById(Mockito.same("0123456789123456"))).thenReturn(Optional.of(member));
        Mockito.when(dsuMock.createDefaultDeviceConfig(Mockito.same("1.0"), Mockito.any(Device.class)))
                .thenReturn(DeviceConfig.builder().id(1L).build());

        DeviceService deviceService = new DeviceService(drMock, srrMock, dcMock, mrMock, dsuMock,null);
        deviceService.setUpVersion("0123456789123456", "012345678912", "1.0");


        ArgumentCaptor<Device> deviceCaptor = ArgumentCaptor.forClass(Device.class);

        Mockito.verify(drMock, Mockito.times(1)).save(deviceCaptor.capture());
        Mockito.verify(mrMock, Mockito.times(1)).save(Mockito.any());

        Device device = deviceCaptor.getValue();

        Assertions.assertEquals(1L, device.getCurrentDeviceConfig().getId());

    }

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