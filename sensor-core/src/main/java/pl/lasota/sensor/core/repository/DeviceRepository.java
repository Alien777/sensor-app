package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.models.device.Device;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {

    @Query("SELECT COUNT(s) > 0 FROM Device s WHERE s.member.memberKey = :memberKey AND s.id = :deviceId")
    boolean existsDevice(@Param("memberKey") String memberKey, @Param("deviceId") String deviceId);

    @Query("SELECT s FROM Device s WHERE s.member.memberKey = :memberKey AND s.id = :deviceId")
    Optional<Device> findDeviceBy(@Param("memberKey") String memberKey, @Param("deviceId") String deviceId);

    @Query("SELECT s FROM Device s WHERE s.member.id = :memberId")
    List<Device> findAllDevicesBy(@Param("memberId") Long memberId);

    @Query("SELECT s FROM Device s WHERE s.member.id = :memberId AND s.id = :deviceId")
    Optional<Device> findDeviceBy(@Param("memberId") Long memberId, @Param("deviceId") String deviceId);

}