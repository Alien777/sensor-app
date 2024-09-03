package pl.lasota.sensor.device.services.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.entities.DeviceToken;

import java.util.UUID;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, String> {

    @Query("SELECT COUNT(s) > 0 FROM DeviceToken s WHERE s.member = :memberId AND s.token = :token")
    boolean isExist(@Param("memberId") String memberId, @Param("token") UUID token);
}