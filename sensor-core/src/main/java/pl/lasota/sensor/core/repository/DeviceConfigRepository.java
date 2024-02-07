package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.models.device.DeviceConfig;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceConfigRepository extends JpaRepository<DeviceConfig, Long> {

    @Query("SELECT s FROM DeviceConfig s WHERE s.device.deviceKey = :deviceKey AND s.id = :configId ORDER BY s.time DESC LIMIT 1")
    Optional<DeviceConfig> getDevice(@Param("deviceKey") String deviceKey, @Param("configId") Long configId);


    @Query("SELECT COUNT(s) > 0 FROM DeviceConfig s WHERE s.device.id = :deviceId")
    boolean existsDeviceConfigBy(@Param("deviceId") Long id);

    @Query("SELECT s FROM DeviceConfig s WHERE s.device.id = :deviceId ORDER BY s.time DESC ")
    List<DeviceConfig> findAllConfigBy(@Param("deviceId") Long deviceId);

}