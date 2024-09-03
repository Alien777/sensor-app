package pl.lasota.sensor.payload.message.pwm;

import pl.lasota.sensor.payload.PayloadParser;

public class PwmWriteSetUpAck implements PayloadParser<PwmWriteSetUpAck, String> {

    public static PwmWriteSetUpAck of(String source) {
        return new PwmWriteSetUpAck();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public PwmWriteSetUpAck revertConvert(String source) {
        return of(source);
    }
}
