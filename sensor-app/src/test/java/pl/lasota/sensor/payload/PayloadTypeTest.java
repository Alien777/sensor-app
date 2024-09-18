package pl.lasota.sensor.payload;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.lasota.sensor.payload.message.analog.AnalogReadOneShotResponse;
import pl.lasota.sensor.payload.message.analog.AnalogReadSetUp;
import pl.lasota.sensor.payload.message.digital.DigitalWriteRequest;
import pl.lasota.sensor.payload.message.pwm.PwmWriteSetUp;

import java.util.UUID;

import static pl.lasota.sensor.payload.PayloadType.ANALOG_READ_SET_UP;

class PayloadTypeTest {

    @Test
    public void pwm_analog_read_set_up_parse() {
        MessageFrame messageFrame = MessageFrame
                .of("7C9EBDF513CC;TIKDvPt3Lu0wYiNg;51c6c380-b3a6-4550-bac0-65781eb7ce0d;ESP-32S;51c6c380-b3a6-4550-bac0-65781eb7ce0d;ANALOG_READ_SET_UP;23;12");
        Assertions.assertEquals("ESP-32S", messageFrame.getVersionFirmware());
        Assertions.assertEquals("7C9EBDF513CC", messageFrame.getDeviceId());
        Assertions.assertEquals("TIKDvPt3Lu0wYiNg", messageFrame.getMemberId());
        Assertions.assertEquals("51c6c380-b3a6-4550-bac0-65781eb7ce0d", messageFrame.getToken().toString());
        Assertions.assertEquals("TIKDvPt3Lu0wYiNg", messageFrame.getMemberId());
        Assertions.assertEquals("51c6c380-b3a6-4550-bac0-65781eb7ce0d", messageFrame.getToken().toString());

        PayloadParser<AnalogReadSetUp, String> stringPayloadParser = MessageFrame.serializePayload(messageFrame.getPayload().convert(), ANALOG_READ_SET_UP);
        AnalogReadSetUp analogReadSetUp = stringPayloadParser.get();
        Assertions.assertEquals(23, analogReadSetUp.getGpio());
        Assertions.assertEquals(12, analogReadSetUp.getWidth());

    }


    @Test
    public void simple_test_digital() {
        UUID uuid = UUID.randomUUID();
        MessageFrame messageFrame = MessageFrame.of("memberId", "deviceId", uuid, "versionFirmware", DigitalWriteRequest.of(2, 1));
        String convert = messageFrame.convert();
        MessageFrame newMessage = MessageFrame.of(convert);
        Assertions.assertEquals("DEVICEID", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals(uuid, newMessage.getToken());
    }

    //
    @Test
    public void simple_test_pwm() {
        UUID uuid = UUID.randomUUID();
        MessageFrame messageFrame = MessageFrame.of("memberId", "deviceId",
                uuid, "versionFirmware", PwmWriteSetUp.of(1, 2, 3, 4));
        String convert = messageFrame.convert();
        MessageFrame newMessage = MessageFrame.of(convert);
        Assertions.assertEquals("versionFirmware", newMessage.getVersionFirmware());
        Assertions.assertEquals("DEVICEID", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals(uuid, newMessage.getToken());


        PwmWriteSetUp pd = (PwmWriteSetUp) newMessage.getPayload().get();
        Assertions.assertEquals(pd.getGpio(), 1);
        Assertions.assertEquals(pd.getFrequency(), 2);
        Assertions.assertEquals(pd.getResolution(), 3);
        Assertions.assertEquals(pd.getDuty(), 4);

    }

    //
    @Test
    public void simple_test_force_analog() {
        UUID uuid = UUID.randomUUID();
        MessageFrame messageFrame = MessageFrame.of("memberId", "deviceId",
                uuid, "versionFirmware", AnalogReadOneShotResponse.of(12, 212));
        String convert = messageFrame.convert();
        MessageFrame newMessage = MessageFrame.of(convert);
        Assertions.assertEquals("versionFirmware", newMessage.getVersionFirmware());
        Assertions.assertEquals("DEVICEID", newMessage.getDeviceId());
        Assertions.assertEquals("memberId", newMessage.getMemberId());
        Assertions.assertEquals(uuid, newMessage.getToken());

        AnalogReadOneShotResponse pd = (AnalogReadOneShotResponse) newMessage.getPayload().get();
        Assertions.assertEquals(pd.getGpio(), 12);
        Assertions.assertEquals(pd.getValue(), 212);

    }


    @Test
    public void simple_check_convert_to_csv() {
        UUID uuid = UUID.randomUUID();
        MessageFrame messageFrame = MessageFrame.of("memberId", "deviceId",
                uuid, "versionFirmware", AnalogReadOneShotResponse.of(12, 212));
        String convert = messageFrame.convert();
        boolean matches = convert.matches("deviceId;memberId;.*;versionFirmware;.*;ANALOG_READ_ONE_SHOT_RESPONSE;12;212");
        Assertions.assertTrue(matches);
    }
}