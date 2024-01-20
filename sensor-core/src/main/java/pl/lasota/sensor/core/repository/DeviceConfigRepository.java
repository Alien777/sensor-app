package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.model.device.DeviceConfig;

import java.util.Optional;

@Repository
public interface DeviceConfigRepository extends JpaRepository<DeviceConfig, Long> {

    @Query("SELECT s FROM DeviceConfig s WHERE s.device.deviceKey = :deviceKey ORDER BY s.time DESC LIMIT 1")
    Optional<DeviceConfig> findLastSensorConfig(@Param("deviceKey") String deviceKey);
}