package pl.lasota.sensor.core.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.lasota.sensor.core.exceptions.NotFoundDefaultConfigException;
import pl.lasota.sensor.core.exceptions.NotFoundMemberException;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.device.Device;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.repository.DeviceConfigRepository;
import pl.lasota.sensor.core.repository.DeviceRepository;
import pl.lasota.sensor.core.repository.MemberRepository;
import pl.lasota.sensor.core.repository.SensorRecordingRepository;

import java.util.Optional;

class DeviceServiceTest {

    @Test
    void not_create_device_if_exist_test() throws NotFoundDefaultConfigException, NotFoundMemberException {

        DeviceRepository drMock = Mockito.mock(DeviceRepository.class);
        SensorRecordingRepository srrMock = Mockito.mock(SensorRecordingRepository.class);
        DeviceConfigRepository dcMock = Mockito.mock(DeviceConfigRepository.class);
        MemberRepository mrMock = Mockito.mock(MemberRepository.class);
        DeviceServiceUtils dsuMock = Mockito.mock(DeviceServiceUtils.class);

        Mockito.when(drMock.existsDevice(Mockito.same("0123456789123456"), Mockito.same("012345678912"))).thenReturn(true);

        DeviceService deviceService = new DeviceService(drMock, srrMock, dcMock, mrMock, dsuMock);
        deviceService.insertNewDevice("0123456789123456", "012345678912", "1.0");

        Mockito.verify(drMock, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void create_device_if_not_exist_test() throws NotFoundDefaultConfigException, NotFoundMemberException {
        Member member = Mockito.mock(Member.class);
        DeviceRepository drMock = Mockito.mock(DeviceRepository.class);
        SensorRecordingRepository srrMock = Mockito.mock(SensorRecordingRepository.class);
        DeviceConfigRepository dcMock = Mockito.mock(DeviceConfigRepository.class);
        MemberRepository mrMock = Mockito.mock(MemberRepository.class);
        DeviceServiceUtils dsuMock = Mockito.mock(DeviceServiceUtils.class);

        Mockito.when(drMock.existsDevice(Mockito.same("0123456789123456"), Mockito.same("012345678912"))).thenReturn(false);
        Mockito.when(mrMock.findMemberByMemberKey(Mockito.same("0123456789123456"))).thenReturn(Optional.of(member));
        Mockito.when(dsuMock.createDefaultDeviceConfig(Mockito.same("1.0"), Mockito.any(Device.class)))
                .thenReturn(DeviceConfig.builder().id(1L).build());

        DeviceService deviceService = new DeviceService(drMock, srrMock, dcMock, mrMock, dsuMock);
        deviceService.insertNewDevice("0123456789123456", "012345678912", "1.0");


        ArgumentCaptor<Device> deviceCaptor = ArgumentCaptor.forClass(Device.class);

        Mockito.verify(drMock, Mockito.times(1)).save(deviceCaptor.capture());
        Mockito.verify(mrMock, Mockito.times(1)).save(Mockito.any());

        Device device = deviceCaptor.getValue();

        Assertions.assertEquals(1L, device.getCurrentDeviceConfig().getId());

    }


}