package pl.lasota.sensor.device.services.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.entities.DeviceTemporary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceTemporaryRepository extends JpaRepository<DeviceTemporary, String> {
    @Query("SELECT s FROM DeviceTemporary s WHERE s.member = :memberId AND s.currentDeviceToken.token = :token")
    Optional<DeviceTemporary> getDeviceTemplate(@Param("memberId") String memberId, @Param("token") UUID token);

    @Query("SELECT s FROM DeviceTemporary s WHERE s.member = :memberId")
    List<DeviceTemporary> findAllDevicesBy(@Param("memberId") String memberId);
}