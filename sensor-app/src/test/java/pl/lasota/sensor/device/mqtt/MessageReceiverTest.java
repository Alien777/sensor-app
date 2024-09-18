package pl.lasota.sensor.device.mqtt;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.lasota.sensor.bus.FlowSensorIInputStreamBus;
import pl.lasota.sensor.bus.WaitForResponseInputStreamBus;
import pl.lasota.sensor.device.services.DeviceDataService;
import pl.lasota.sensor.device.services.filters.BeforeValidMessageFilter;
import pl.lasota.sensor.device.services.filters.BroadcastSensorValueFilter;
import pl.lasota.sensor.device.services.filters.Filter;
import pl.lasota.sensor.device.services.filters.FilterChain;
import pl.lasota.sensor.member.services.MemberLoginService;
import pl.lasota.sensor.payload.MessageFrame;
import pl.lasota.sensor.payload.PayloadType;

import java.util.UUID;


class MessageReceiverTest {


    @Test
    public void execute_filter_test() throws Exception {
        MessageFrame mfMock = Mockito.mock(MessageFrame.class);
        MemberLoginService msMock = Mockito.mock(MemberLoginService.class);
        DeviceDataService dsMock = Mockito.mock(DeviceDataService.class);

        UUID uuid = UUID.randomUUID();
        Mockito.when(mfMock.getMemberId()).thenReturn("memberId_1234567");
        Mockito.when(mfMock.getDeviceId()).thenReturn("deviceId_123");
        Mockito.when(mfMock.getRequestId()).thenReturn(UUID.randomUUID());
        Mockito.when(mfMock.getToken()).thenReturn(uuid);
        Mockito.when(mfMock.getVersionFirmware()).thenReturn("1.0");
        Mockito.when(mfMock.getPayloadType()).thenReturn(PayloadType.CONNECTED_ACK);
        Mockito.when(msMock.isMemberExistByMemberId(Mockito.same("memberId_1234567"))).thenReturn(true);
        Mockito.when(dsMock.moveToDeviceFromTemporary("memberId_1234567", "deviceId_123", uuid)).thenReturn(true);
        Mockito.when(dsMock.isCurrentTokenValid("memberId_1234567", "deviceId_123", uuid)).thenReturn(true);
        Mockito.when(dsMock.isDeviceExist("memberId_1234567", "deviceId_123")).thenReturn(true);


        FilterChain filterChain = new FilterChain();
        BeforeValidMessageFilter beforeValidMessageFilter = new BeforeValidMessageFilter(dsMock);
        BroadcastSensorValueFilter broadcastSensorValueFilter = new BroadcastSensorValueFilter(new FlowSensorIInputStreamBus(), new WaitForResponseInputStreamBus());

        Filter mock = Mockito.mock(Filter.class);

        filterChain
                .addFilter(beforeValidMessageFilter)
                .addFilter(broadcastSensorValueFilter)
                .addFilter(mock);

        filterChain.doFilter(mfMock);

        Mockito.verify(mock, Mockito.times(1)).execute(Mockito.any(), Mockito.any(), Mockito.any());

    }
}
