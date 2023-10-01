package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.model.SensorConfig;

import java.util.Optional;

@Repository
public interface SensorConfigRepository extends JpaRepository<SensorConfig, Long> {

    @Query("SELECT s FROM SensorConfig s WHERE s.sensor.device_key = :deviceKey ORDER BY s.time DESC LIMIT 1")
    Optional<SensorConfig> findLastSensorConfig(@Param("deviceKey") String deviceKey);
}