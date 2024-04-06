package pl.lasota.sensor.api.mqtt;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.client.RestTemplate;
import pl.lasota.sensor.api.filter.FilterChain;
import pl.lasota.sensor.api.filter.filters.BeforeValidMessageFilter;
import pl.lasota.sensor.api.filter.filters.SaveSensorValueFilter;
import pl.lasota.sensor.core.apis.FlowsMicroserviceEndpoint;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageType;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.MemberService;


class MessageReceiverTest {


    @Test
    public void execute_post_filter_test() throws Exception {
        MessageFrame mfMock = Mockito.mock(MessageFrame.class);
        MemberService msMock = Mockito.mock(MemberService.class);
        DeviceService dsMock = Mockito.mock(DeviceService.class);
        MqttPreSendLayout uMock = Mockito.mock(MqttPreSendLayout.class);
        FlowsMicroserviceEndpoint crtMock = Mockito.mock(FlowsMicroserviceEndpoint.class);
        DiscoveryClient dcMock = Mockito.mock(DiscoveryClient.class);
        RestTemplate rtMock = Mockito.mock(RestTemplate.class);


        Mockito.when(mfMock.getMemberId()).thenReturn("memberId_1234567");
        Mockito.when(mfMock.getDeviceId()).thenReturn("deviceId_123");
        Mockito.when(mfMock.getToken()).thenReturn("token");
        Mockito.when(mfMock.getVersionFirmware()).thenReturn("1.0");
        Mockito.when(mfMock.getMessageType()).thenReturn(MessageType.DEVICE_CONNECTED);
        Mockito.when(msMock.isMemberExistByMemberId(Mockito.same("memberId_1234567"))).thenReturn(true);
        Mockito.when(dsMock.isTokenValid("memberId_1234567", "deviceId_123", "token")).thenReturn(true);


        FilterChain filterChain = new FilterChain();
        BeforeValidMessageFilter beforeValidMessageFilter = new BeforeValidMessageFilter(msMock, dsMock);
        SaveSensorValueFilter saveSensorValueFilter = new SaveSensorValueFilter(dsMock, uMock, crtMock, dcMock, rtMock);
        filterChain
                .addFilter(beforeValidMessageFilter)
                .addFilter(saveSensorValueFilter);

        filterChain.doFilter(mfMock);

        Mockito.verify(uMock, Mockito.times(1)).sendConfig(Mockito.same("memberId_1234567"),
                Mockito.same("deviceId_123"));

    }


}