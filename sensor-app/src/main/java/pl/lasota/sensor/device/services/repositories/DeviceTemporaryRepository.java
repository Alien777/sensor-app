package pl.lasota.sensor.device.services.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.entities.DeviceTemporary;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTemporaryRepository extends JpaRepository<DeviceTemporary, String> {
    @Query("SELECT s FROM DeviceTemporary s WHERE s.member = :memberId AND s.id = :deviceId AND s.currentDeviceToken.token = :token")
    Optional<DeviceTemporary> isTokenValid(@Param("memberId") String memberId, @Param("token") String token);

    @Query("SELECT s FROM DeviceTemporary s WHERE s.member = :memberId")
    List<DeviceTemporary> findAllDevicesBy(@Param("memberId") String memberId);
}