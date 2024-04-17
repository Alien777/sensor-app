package pl.lasota.sensor.api.mqtt;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.client.RestClient;
import pl.lasota.sensor.api.apis.mqtt.MqttPreSendLayout;
import pl.lasota.sensor.api.payload.MessageFrame;
import pl.lasota.sensor.api.payload.MessageType;
import pl.lasota.sensor.api.process.FilterChain;
import pl.lasota.sensor.api.process.filters.BeforeValidMessageFilter;
import pl.lasota.sensor.api.process.filters.SaveSensorValueFilter;
import pl.lasota.sensor.api.services.DeviceService;
import pl.lasota.sensor.internal.apis.api.FlowsMicroserviceEndpoint;
import pl.lasota.sensor.member.services.MemberService;


class MessageReceiverTest {


    @Test
    public void execute_post_filter_test() throws Exception {
        MessageFrame mfMock = Mockito.mock(MessageFrame.class);
        MemberService msMock = Mockito.mock(MemberService.class);
        DeviceService dsMock = Mockito.mock(DeviceService.class);
        MqttPreSendLayout uMock = Mockito.mock(MqttPreSendLayout.class);
        FlowsMicroserviceEndpoint crtMock = Mockito.mock(FlowsMicroserviceEndpoint.class);
        DiscoveryClient dcMock = Mockito.mock(DiscoveryClient.class);
        RestClient rtMock = Mockito.mock(RestClient.class);


        Mockito.when(mfMock.getMemberId()).thenReturn("memberId_1234567");
        Mockito.when(mfMock.getDeviceId()).thenReturn("deviceId_123");
        Mockito.when(mfMock.getToken()).thenReturn("token");
        Mockito.when(mfMock.getVersionFirmware()).thenReturn("1.0");
        Mockito.when(mfMock.getMessageType()).thenReturn(MessageType.DEVICE_CONNECTED);
        Mockito.when(msMock.isMemberExistByMemberId(Mockito.same("memberId_1234567"))).thenReturn(true);
        Mockito.when(dsMock.isTokenValid("memberId_1234567", "deviceId_123", "token")).thenReturn(true);
        Mockito.when(dsMock.isDeviceExist("memberId_1234567", "deviceId_123")).thenReturn(true);


        FilterChain filterChain = new FilterChain();
        BeforeValidMessageFilter beforeValidMessageFilter = new BeforeValidMessageFilter(dsMock);
        SaveSensorValueFilter saveSensorValueFilter = new SaveSensorValueFilter(dsMock, uMock, crtMock, dcMock, rtMock);
        filterChain
                .addFilter(beforeValidMessageFilter)
                .addFilter(saveSensorValueFilter);

        filterChain.doFilter(mfMock);

        Mockito.verify(uMock, Mockito.times(1)).sendConfig(Mockito.same("memberId_1234567"),
                Mockito.same("deviceId_123"));

    }
}
