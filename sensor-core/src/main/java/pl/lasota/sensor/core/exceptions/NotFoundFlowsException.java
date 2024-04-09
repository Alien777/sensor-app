package pl.lasota.sensor.core.exceptions;


public class NotFoundFlowsException extends SensorException {

    public NotFoundFlowsException() {
        super();
    }

    public NotFoundFlowsException(Throwable e) {
        super(e);
    }

    @Override
    public String getCode() {
        return "NFF";
    }
}
