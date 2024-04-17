package pl.lasota.sensor.api.process.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.payload.MessageFrame;
import pl.lasota.sensor.api.process.Chain;
import pl.lasota.sensor.api.process.FilterContext;
import pl.lasota.sensor.api.process.Filter;
import pl.lasota.sensor.api.services.DeviceService;


@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class SetUpDeviceFilter implements Filter<MessageFrame, MessageFrame> {

    private final DeviceService deviceService;

    @Override
    public void execute(MessageFrame request, FilterContext context, Chain<MessageFrame> chain) throws Exception {
        deviceService.setUpVersion(request.getMemberId(), request.getDeviceId(), request.getVersionFirmware());
        chain.doFilter(request);
    }
}
