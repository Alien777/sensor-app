package pl.lasota.sensor.device.services.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.entities.Sensor;


@Repository
public interface SensorRecordingRepository extends JpaRepository<Sensor, Long> {
}
