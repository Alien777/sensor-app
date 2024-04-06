package pl.lasota.sensor.core.entities.sensor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class AnalogSensor extends Sensor {

    @Column(name = "adc_raw")
    private double adcRaw;

    @Column(name = "pin")
    private int pin;
}
