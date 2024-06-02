package pl.lasota.sensor.bus;

import java.io.OutputStream;

public interface Synchro<STREAM_OUT extends OutputStream> {
    void synchro(STREAM_OUT streamOut);
}