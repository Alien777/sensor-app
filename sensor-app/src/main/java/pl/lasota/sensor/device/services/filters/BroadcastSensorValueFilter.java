package pl.lasota.sensor.device.services.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.bus.FlowSensorIInputStreamBus;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.payload.MessageFrame;


@Component
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class BroadcastSensorValueFilter implements Filter<MessageFrame, MessageFrame> {

    private final FlowSensorIInputStreamBus queueWithMessage;


    @Override
    public void execute(MessageFrame request, FilterContext filterContext, Chain<MessageFrame> chain) throws Exception {
        FlowSensorI flowSensorI = FlowSensorI.of(request.getPayloadType(), request.getMemberId(), request.getMemberId(), request.getRequestId(), request.getPayload());
        queueWithMessage.takeBroadcaster(null).write(flowSensorI);
        chain.doFilter(request);
    }

}
