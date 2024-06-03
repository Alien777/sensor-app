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
public class BeforeValidMessageFilter implements Filter<MessageFrame, MessageFrame> {

    public static final int MAC_SIZE = 12;
    public static final int MEMBER_KEY_SIZE = 16;

    private final DeviceDataService ds;

    @Override
    public void execute(MessageFrame request, FilterContext filterContext, Chain<MessageFrame> chain) throws Exception {
        if (request.getMemberId().trim().isBlank() || request.getMemberId().length() != MEMBER_KEY_SIZE) {
            log.info("Member key is wrong {}", request.getMemberId());
            return;
        }

        if (request.getDeviceId().trim().isBlank() || request.getDeviceId().length() != MAC_SIZE) {
            log.info("Device key is wrong {} ", request.getDeviceId());
            return;
        }

        if (request.getDeviceId().trim().isBlank()) {
            log.info("Token key is wrong {} ", request.getDeviceId());
            return;
        }

        if (request.getVersionFirmware().trim().isBlank()) {
            log.info("Version of firmware is obligatory {} ", request.getDeviceId());
            return;
        }

        if (!ds.isDeviceExist(request.getMemberId(), request.getDeviceId())) {

            if(!ds.moveToDeviceFromTemporary(request.getMemberId(), request.getDeviceId(), request.getToken()))
            {
                log.info("Device not existing {} ", request.getDeviceId());
                return;
            }
        }

        if (!ds.isTokenValid(request.getMemberId(), request.getDeviceId(), request.getToken())) {
            log.info("Wrong token {} ", request.getDeviceId());
            return;
        }

        chain.doFilter(request);
    }
}
