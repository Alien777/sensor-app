package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.model.Sensor;

import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

    @Query("SELECT COUNT(s) > 0 FROM Sensor s WHERE s.member.memberKey = :memberKey AND s.device_key = :deviceKey")
    boolean existsSensor(@Param("memberKey") String memberKey, @Param("deviceKey") String deviceKey);

    @Query("SELECT s FROM Sensor s WHERE s.member.memberKey = :memberKey AND s.device_key = :deviceKey")
    Optional<Sensor> findSensorBy(@Param("memberKey") String memberKey, @Param("deviceKey") String deviceKey);
}