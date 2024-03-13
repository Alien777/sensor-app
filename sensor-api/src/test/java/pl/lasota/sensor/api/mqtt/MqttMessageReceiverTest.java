package pl.lasota.sensor.api.mqtt;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.lasota.sensor.api.filter.FilterChain;
import pl.lasota.sensor.api.filter.filters.IsExistMemberFilter;
import pl.lasota.sensor.api.filter.filters.SaveSensorValueFilter;
import pl.lasota.sensor.core.restapi.SensorFlowsHelper;
import pl.lasota.sensor.core.models.mqtt.payload.MessageType;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.MemberService;

class MqttMessageReceiverTest {


    @Test
    public void execute_post_filter_test() throws Exception {
        MessageFrame mfMock = Mockito.mock(MessageFrame.class);
        MemberService msMock = Mockito.mock(MemberService.class);
        DeviceService dsMock = Mockito.mock(DeviceService.class);
        MqttPreSendLayout uMock = Mockito.mock(MqttPreSendLayout.class);
        SensorFlowsHelper crtMock = Mockito.mock(SensorFlowsHelper.class);


        Mockito.when(mfMock.getMemberKey()).thenReturn("0123456789123456");
        Mockito.when(mfMock.getDeviceId()).thenReturn("012345678912");
        Mockito.when(mfMock.getVersionFirmware()).thenReturn("1.0");
        Mockito.when(mfMock.getMessageType()).thenReturn(MessageType.DEVICE_CONNECTED);
        Mockito.when(msMock.isMemberExistByMemberKey(Mockito.same("0123456789123456"))).thenReturn(true);


        FilterChain filterChain = new FilterChain();
        IsExistMemberFilter isExistMemberFilter = new IsExistMemberFilter(msMock);
        SaveSensorValueFilter saveSensorValueFilter = new SaveSensorValueFilter(dsMock, uMock, crtMock);
        filterChain
                .addFilter(isExistMemberFilter)
                .addFilter(saveSensorValueFilter);

        filterChain.doFilter(mfMock);

        Mockito.verify(uMock, Mockito.times(1)).sendConfig(Mockito.same("0123456789123456"),
                Mockito.same("012345678912"));

    }


}