package pl.lasota.sensor.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class AnalogAckSensor extends Sensor {

    @Column(name = "adc_raw")
    private double adcRaw;

    @Column(name = "pin")
    private int pin;
}
