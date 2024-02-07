package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.models.device.Device;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("SELECT COUNT(s) > 0 FROM Device s WHERE s.member.memberKey = :memberKey AND s.deviceKey = :deviceKey")
    boolean existsDevice(@Param("memberKey") String memberKey, @Param("deviceKey") String deviceKey);

    @Query("SELECT s FROM Device s WHERE s.member.memberKey = :memberKey AND s.deviceKey = :deviceKey")
    Optional<Device> findDeviceBy(@Param("memberKey") String memberKey, @Param("deviceKey") String deviceKey);

    @Query("SELECT s FROM Device s WHERE s.member.memberKey = :memberKey")
    List<Device> findAllDevicesBy(@Param("memberKey") String memberKey);
}