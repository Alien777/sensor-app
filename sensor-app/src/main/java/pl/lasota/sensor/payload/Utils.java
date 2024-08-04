package pl.lasota.sensor.payload;

public class Utils {

    public static Integer integer(String value) {
        return value == null || value.isEmpty() || "null".equalsIgnoreCase(value) ? null : Integer.valueOf(value.trim());
    }

}
