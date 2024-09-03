package pl.lasota.sensor.payload.message.pwm;

import pl.lasota.sensor.payload.PayloadParser;

public class PwmWriteTearDownAck implements PayloadParser<PwmWriteTearDownAck, String> {


    public static PwmWriteTearDownAck of(String source) {
        return new PwmWriteTearDownAck();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public PwmWriteTearDownAck revertConvert(String source) {
        return of(source);
    }
}
