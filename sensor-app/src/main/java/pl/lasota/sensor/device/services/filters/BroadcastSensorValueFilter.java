package pl.lasota.sensor.device.services.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.bus.FlowSensorIInputStreamBus;
import pl.lasota.sensor.bus.WaitForResponseInputStreamBus;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.payload.MessageFrame;


@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class BroadcastSensorValueFilter implements Filter<MessageFrame, MessageFrame> {

    private final FlowSensorIInputStreamBus queueWithMessage;
    private final WaitForResponseInputStreamBus queueToWaitForMessage;

    @Override
    public void execute(MessageFrame request, FilterContext filterContext, Chain<MessageFrame> chain) throws Exception {

        queueToWaitForMessage.takeBroadcaster(null).write(request.getRequestId());

        FlowSensorI flowSensorI = new FlowSensorI().setDeviceId(request.getDeviceId())
                .setMemberId(request.getMemberId())
                .setPayloadType(request.getPayloadType());

        queueWithMessage.takeBroadcaster(null).write(flowSensorI);

        chain.doFilter(request);
    }

}
