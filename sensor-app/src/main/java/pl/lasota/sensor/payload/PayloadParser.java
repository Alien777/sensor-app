package pl.lasota.sensor.payload;

/**
 * @hidden
 */
public interface PayloadParser<A, B> {
    /**
     * @hidden
     */
    B convert();

    A revertConvert(B source);

   default A get(){
       return this.revertConvert(this.convert());
   }
}
