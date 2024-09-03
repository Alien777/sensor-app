package pl.lasota.sensor.device.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.entities.Device;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class PingServiceJob {

    private final DeviceDataService deviceDataService;
    private final DeviceMessagePublish mf;

    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void run() {
        List<Device> allDevices = deviceDataService.getAllDevices();
        for (Device allDevice : allDevices) {
            try {
                mf.sendPing(allDevice.getMember(), allDevice.getId());
            } catch (Exception e) {
                log.error("Problem with send ping {}", allDevice.getId(), e);
            }
        }
    }
}
