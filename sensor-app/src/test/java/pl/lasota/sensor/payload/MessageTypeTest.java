package pl.lasota.sensor.payload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.lasota.sensor.payload.to.*;
import pl.lasota.sensor.payload.to.dependet.AnalogConfig;
import pl.lasota.sensor.payload.to.dependet.DigitalConfig;
import pl.lasota.sensor.payload.to.dependet.PwmConfig;

import java.util.List;
import java.util.UUID;

class MessageTypeTest {

    @Test
    public void simple_test_config() throws JsonProcessingException {
        ConfigPayload configPayload = new ConfigPayload();
        MessageFrame messageFrame = MessageFrame.factoryConfigPayload(1L, "version", "deviceId", "memberId", "token", configPayload);
        String convert = messageFrame.convert();
        MessageFrame newMessage = new MessageFrame().revertConvert(convert);
        Assertions.assertEquals(1L, newMessage.getConfigIdentifier());
        Assertions.assertEquals("version", newMessage.getVersionFirmware());
        Assertions.assertEquals("DEVICEID", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals("token", newMessage.getToken());
    }

    @Test
    public void extended_test_config() throws JsonProcessingException {

        String s = """
                {"pwm_configs": [{"pin": 23, "freq": 1000, "resolution": 13}], "analog_configs": [{"pin": 4, "atten": 3, "width": 12},{"pin": 4, "atten": 3, "width": 12}], "digital_configs": [{"pin": 22}, {"pin": 21}]}
                """;
        ObjectMapper objectMapper = new ObjectMapper();
        ConfigPayload configPayload1 = objectMapper.readValue(s, ConfigPayload.class);

        MessageFrame messageFrame = MessageFrame.factoryConfigPayload(1L, "version", "deviceId", "memberId", "token", configPayload1);
        String convert = messageFrame.convert();
        MessageFrame newMessage = new MessageFrame().revertConvert(convert);
        Assertions.assertEquals(1L, newMessage.getConfigIdentifier());
        Assertions.assertEquals("version", newMessage.getVersionFirmware());
        Assertions.assertEquals("DEVICEID", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals("token", newMessage.getToken());

        ConfigPayload payloadFromDriver = (ConfigPayload) newMessage.getPayloadFromDriver(newMessage.getPayload());

        List<DigitalConfig> digitalConfig = payloadFromDriver.getDigitalConfig();
        DigitalConfig dc = digitalConfig.get(0);
        DigitalConfig dc2 = digitalConfig.get(1);
        Assertions.assertEquals(dc.getPin(), 22);
        Assertions.assertEquals(dc2.getPin(), 21);

        List<AnalogConfig> analogReader = payloadFromDriver.getAnalogReader();
        AnalogConfig ac = analogReader.get(0);
        Assertions.assertEquals(ac.getPin(), 4);
        Assertions.assertEquals(ac.getWidth(), 12);
        Assertions.assertEquals(ac.getAtten(), 3);


        ac = analogReader.get(1);
        Assertions.assertEquals(ac.getPin(), 4);
        Assertions.assertEquals(ac.getWidth(), 12);
        Assertions.assertEquals(ac.getAtten(), 3);


        List<PwmConfig> pwmConfig = payloadFromDriver.getPwmConfig();
        PwmConfig pc = pwmConfig.get(0);
        Assertions.assertEquals(pc.getPin(), 23);
        Assertions.assertEquals(pc.getFreq(), 1000);
        Assertions.assertEquals(pc.getResolution(), 13);
    }

    @Test
    public void extended_test_config_2() throws JsonProcessingException {

        String s = """
                {"pwm_configs": [], "analog_configs": [{"pin": 4, "atten": 3, "width": 12},{"pin": 4, "atten": 3, "width": 12}], "digital_configs": [{"pin": 22}, {"pin": 21}]}
                """;
        ObjectMapper objectMapper = new ObjectMapper();
        ConfigPayload configPayload1 = objectMapper.readValue(s, ConfigPayload.class);

        MessageFrame messageFrame = MessageFrame.factoryConfigPayload(1L, "version", "deviceId", "memberId", "token", configPayload1);
        String convert = messageFrame.convert();
        MessageFrame newMessage = new MessageFrame().revertConvert(convert);
        Assertions.assertEquals(1L, newMessage.getConfigIdentifier());
        Assertions.assertEquals("version", newMessage.getVersionFirmware());
        Assertions.assertEquals("DEVICEID", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals("token", newMessage.getToken());

        ConfigPayload payloadFromDriver = (ConfigPayload) newMessage.getPayloadFromDriver(newMessage.getPayload());

        List<DigitalConfig> digitalConfig = payloadFromDriver.getDigitalConfig();
        DigitalConfig dc = digitalConfig.get(0);
        DigitalConfig dc2 = digitalConfig.get(1);
        Assertions.assertEquals(dc.getPin(), 22);
        Assertions.assertEquals(dc2.getPin(), 21);

        List<AnalogConfig> analogReader = payloadFromDriver.getAnalogReader();
        AnalogConfig ac = analogReader.get(0);
        Assertions.assertEquals(ac.getPin(), 4);
        Assertions.assertEquals(ac.getWidth(), 12);
        Assertions.assertEquals(ac.getAtten(), 3);


        ac = analogReader.get(1);
        Assertions.assertEquals(ac.getPin(), 4);
        Assertions.assertEquals(ac.getWidth(), 12);
        Assertions.assertEquals(ac.getAtten(), 3);


        List<PwmConfig> pwmConfig = payloadFromDriver.getPwmConfig();
        Assertions.assertEquals(pwmConfig.size(), 0);

    }


    @Test
    public void simple_test_digital() {
        UUID uuid = UUID.randomUUID();
        MessageFrame messageFrame = MessageFrame.factoryDigitalPayload(1L, "version", "deviceId", "memberId", uuid.toString(), new DigitalPayload(1, 2));
        String convert = messageFrame.convert();
        MessageFrame newMessage = new MessageFrame().revertConvert(convert);
        Assertions.assertEquals(1L, newMessage.getConfigIdentifier());
        Assertions.assertEquals("version", newMessage.getVersionFirmware());
        Assertions.assertEquals("DEVICEID", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals(uuid.toString(), newMessage.getToken());

        DigitalPayload pd = (DigitalPayload) newMessage.getPayloadFromDriver(newMessage.getPayload());

        Assertions.assertEquals(pd.getPin(), 1);
        Assertions.assertEquals(pd.getValue(), 2);
    }

    @Test
    public void simple_test_pwm() {
        UUID uuid = UUID.randomUUID();
        MessageFrame messageFrame = MessageFrame.factoryPwmPayload(1L, "version", "deviceId",
                "memberId", uuid.toString(), new PwmPayload(1, 2, 3));
        String convert = messageFrame.convert();
        MessageFrame newMessage = new MessageFrame().revertConvert(convert);
        Assertions.assertEquals(1L, newMessage.getConfigIdentifier());
        Assertions.assertEquals("version", newMessage.getVersionFirmware());
        Assertions.assertEquals("DEVICEID", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals(uuid.toString(), newMessage.getToken());

        PwmPayload pd = (PwmPayload) newMessage.getPayloadFromDriver(newMessage.getPayload());

        Assertions.assertEquals(pd.getPin(), 1);
        Assertions.assertEquals(pd.getDuty(), 2);
        Assertions.assertEquals(pd.getDuration(), 3);
    }

    @Test
    public void simple_test_force_analog() {
        UUID uuid = UUID.randomUUID();
        MessageFrame messageFrame = MessageFrame.factorySendForAnalogData(1L, "version", "deviceId",
                "memberId", uuid.toString(), new AnalogDataPayload(12));

        String convert = messageFrame.convert();
        MessageFrame newMessage = new MessageFrame().revertConvert(convert);
        Assertions.assertEquals(1L, newMessage.getConfigIdentifier());
        Assertions.assertEquals("version", newMessage.getVersionFirmware());
        Assertions.assertEquals("DEVICEID", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals(uuid.toString(), newMessage.getToken());

        AnalogDataPayload pd = (AnalogDataPayload) newMessage.getPayloadFromDriver(newMessage.getPayload());

        Assertions.assertEquals(pd.getPin(), 12);

    }

    @Test
    public void simple_test_ping() {
        UUID uuid = UUID.randomUUID();
        MessageFrame messageFrame = MessageFrame.factorySendPingData(1L, "version", "deviceId",
                "memberId", uuid.toString(), new PingPayload());

        String convert = messageFrame.convert();
        MessageFrame newMessage = new MessageFrame().revertConvert(convert);
        Assertions.assertEquals(1L, newMessage.getConfigIdentifier());
        Assertions.assertEquals("version", newMessage.getVersionFirmware());
        Assertions.assertEquals("DEVICEID", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals(uuid.toString(), newMessage.getToken());

        PingPayload pd = (PingPayload) newMessage.getPayloadFromDriver(newMessage.getPayload());

        Assertions.assertTrue(pd instanceof PingPayload);

    }

    @Test
    public void simple_test_no_data() {
        UUID uuid = UUID.randomUUID();
        MessageFrame messageFrame = MessageFrame.factorySendPingData(1L, "version", null,
                "memberId", uuid.toString(), new PingPayload());

        String convert = messageFrame.convert();
        MessageFrame newMessage = new MessageFrame().revertConvert(convert);
        Assertions.assertEquals(1L, newMessage.getConfigIdentifier());
        Assertions.assertEquals("version", newMessage.getVersionFirmware());
        Assertions.assertEquals("NULL", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals(uuid.toString(), newMessage.getToken());

        PingPayload pd = (PingPayload) newMessage.getPayloadFromDriver(newMessage.getPayload());

        Assertions.assertTrue(pd instanceof PingPayload);

    }
}