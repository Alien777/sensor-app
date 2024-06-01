package pl.lasota.sensor.device.services.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.entities.Device;
import pl.lasota.sensor.entities.DeviceConfig;

import java.util.LinkedList;
import java.util.Optional;


@Repository
public interface DeviceConfigRepository extends JpaRepository<DeviceConfig, Long> {

    @Query("SELECT s FROM DeviceConfig s WHERE s.device.id = :deviceId AND s.id = :configId ORDER BY s.time DESC LIMIT 1")
    Optional<DeviceConfig> getDeviceConfig(@Param("deviceId") String deviceId, @Param("configId") Long configId);

    @Query("SELECT COUNT(s) > 0 FROM DeviceConfig s WHERE s.device.id = :deviceId")
    boolean existsDeviceConfigBy(@Param("deviceId") String deviceId);

    @Query("SELECT s FROM DeviceConfig s WHERE s.device.id = :deviceId ORDER BY s.time DESC ")
    LinkedList<DeviceConfig> findAllDeviceConfigBy(@Param("deviceId") String deviceId);

    @Query("SELECT s FROM DeviceConfig s WHERE s.checksum = :checksum AND s.device.id = :deviceId")
    Optional<DeviceConfig> getConfigByChecksum(@Param("checksum") Long checksum, @Param("deviceId") String deviceId);

    @Query("SELECT s FROM Device s WHERE s.id = :deviceId AND s.member = :memberId")
    Optional<Device> getDevice(@Param("memberId") String memberId, @Param("deviceId") String deviceId);

    @Query("SELECT s FROM  DeviceConfig s WHERE s.device.member= :memberId AND s.device.id = :deviceId AND s.id = :configId")
    DeviceConfig getDeviceConfigById(@Param("memberId") String memberId, @Param("deviceId") String deviceId, @Param("configId") String configId);
}