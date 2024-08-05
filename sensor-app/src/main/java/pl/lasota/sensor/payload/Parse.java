package pl.lasota.sensor.payload;

/**
 * @hidden
 */
public interface Parse<A, B> {
    /**
     * @hidden
     */
    B convert();

    A revertConvert(B source);

}
