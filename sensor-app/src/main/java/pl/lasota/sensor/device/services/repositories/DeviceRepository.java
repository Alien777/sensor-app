package pl.lasota.sensor.device.services.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.entities.Device;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {

    @Query("SELECT COUNT(s) > 0 FROM Device s WHERE s.member = :memberId AND s.id = :deviceId")
    boolean existsDevice(@Param("memberId") String memberId, @Param("deviceId") String deviceId);

    @Query("SELECT COUNT(s) > 0 FROM Device s WHERE s.member = :memberId AND s.id = :deviceId AND s.currentDeviceToken.token = :token")
    boolean isCurrentTokenValid(@Param("memberId") String memberId, @Param("deviceId") String deviceId, @Param("token") String token);

    @Query("SELECT s FROM Device s WHERE s.member = :memberId AND s.version IS NOT NULL")
    List<Device> findAllDevicesBy(@Param("memberId") String memberId);

    @Query("SELECT s FROM Device s WHERE s.member = :memberId AND s.id = :deviceId")
    Optional<Device> findDeviceBy(@Param("memberId") String memberId, @Param("deviceId") String deviceId);

}