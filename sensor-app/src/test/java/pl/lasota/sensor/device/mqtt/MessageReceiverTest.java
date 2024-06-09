package pl.lasota.sensor.device.mqtt;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.lasota.sensor.bus.FlowSensorIInputStreamBus;
import pl.lasota.sensor.device.services.DeviceDataService;
import pl.lasota.sensor.device.services.DeviceMessagePublish;
import pl.lasota.sensor.device.services.filters.BeforeValidMessageFilter;
import pl.lasota.sensor.device.services.filters.FilterChain;
import pl.lasota.sensor.device.services.filters.SaveSensorValueFilter;
import pl.lasota.sensor.member.MemberService;
import pl.lasota.sensor.payload.MessageFrame;
import pl.lasota.sensor.payload.MessageType;


class MessageReceiverTest {


    @Test
    public void execute_post_filter_test() throws Exception {
        MessageFrame mfMock = Mockito.mock(MessageFrame.class);
        MemberService msMock = Mockito.mock(MemberService.class);
        DeviceDataService dsMock = Mockito.mock(DeviceDataService.class);
        DeviceMessagePublish uMock = Mockito.mock(DeviceMessagePublish.class);


        Mockito.when(mfMock.getMemberId()).thenReturn("memberId_1234567");
        Mockito.when(mfMock.getDeviceId()).thenReturn("deviceId_123");
        Mockito.when(mfMock.getToken()).thenReturn("token");
        Mockito.when(mfMock.getVersionFirmware()).thenReturn("1.0");
        Mockito.when(mfMock.getMessageType()).thenReturn(MessageType.DEVICE_CONNECTED);
        Mockito.when(msMock.isMemberExistByMemberId(Mockito.same("memberId_1234567"))).thenReturn(true);
        Mockito.when(dsMock.moveToDeviceFromTemporary("memberId_1234567", "deviceId_123", "token")).thenReturn(true);
        Mockito.when(dsMock.isCurrentTokenValid("memberId_1234567", "deviceId_123", "token")).thenReturn(true);
        Mockito.when(dsMock.isDeviceExist("memberId_1234567", "deviceId_123")).thenReturn(true);


        FilterChain filterChain = new FilterChain();
        BeforeValidMessageFilter beforeValidMessageFilter = new BeforeValidMessageFilter(dsMock);
        SaveSensorValueFilter saveSensorValueFilter = new SaveSensorValueFilter(dsMock, uMock, new FlowSensorIInputStreamBus());
        filterChain
                .addFilter(beforeValidMessageFilter)
                .addFilter(saveSensorValueFilter);

        filterChain.doFilter(mfMock);

        Mockito.verify(uMock, Mockito.times(1)).sendConfig(Mockito.same("memberId_1234567"),
                Mockito.same("deviceId_123"));

    }
}
