package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.model.SensorReading;

@Repository
public interface SensorRecordingRepository extends JpaRepository<SensorReading, Long> {
}
