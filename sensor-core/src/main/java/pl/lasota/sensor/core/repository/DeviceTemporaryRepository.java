package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.entities.device.DeviceTemporary;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTemporaryRepository extends JpaRepository<DeviceTemporary, String> {
    @Query("SELECT s FROM DeviceTemporary s WHERE s.member.id = :memberId AND s.id = :deviceId AND s.currentDeviceToken.token = :token")
    Optional<DeviceTemporary> isTokenValid(@Param("memberId") String memberId, @Param("token") String token);

    @Query("SELECT s FROM DeviceTemporary s WHERE s.member.id = :memberId")
    List<DeviceTemporary> findAllDevicesBy(@Param("memberId") String memberId);
}