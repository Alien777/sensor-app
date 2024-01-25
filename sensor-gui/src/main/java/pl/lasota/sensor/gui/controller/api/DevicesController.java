package pl.lasota.sensor.gui.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.device.Device;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.MemberService;
import pl.lasota.sensor.gui.model.DeviceT;

import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DevicesController {

    private final DeviceService ds;

    private final MemberService ms;

    @GetMapping
    public List<DeviceT> devices() {
        Member member = ms.loggedUser();
        return ds.getAllDevice(member.getMemberKey()).stream().map(DeviceT::map).collect(Collectors.toList());
    }
}
