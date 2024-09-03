package pl.lasota.sensor.device.services.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.payload.MessageFrame;
import pl.lasota.sensor.device.services.DeviceDataService;


@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public  class SetUpDeviceFilter implements Filter<MessageFrame, MessageFrame> {

    private final DeviceDataService deviceDataService;

    @Override
    public void execute(MessageFrame request, FilterContext context, Chain<MessageFrame> chain) throws Exception {
        chain.doFilter(request);
    }
}
