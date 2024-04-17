package pl.lasota.sensor.api.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.api.entities.Sensor;


@Repository
public interface SensorRecordingRepository extends JpaRepository<Sensor, Long> {
}
