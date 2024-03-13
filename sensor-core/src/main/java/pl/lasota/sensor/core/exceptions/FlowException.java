package pl.lasota.sensor.core.exceptions;


public class FlowException extends SensorException {


    public FlowException(Throwable e) {
        super(e);
    }

    public FlowException(String e) {
        super(e);
    }


    @Override
    public String getCode() {
        return "FE";
    }
}
