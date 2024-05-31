package pl.lasota.sensor.api.apis;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.lasota.sensor.core.exceptions.SensorException;
import pl.lasota.sensor.member.entities.Member;
import pl.lasota.sensor.member.services.MemberService;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingControllerAspect {

    private final MemberService memberService;

    @Before("execution(* pl.lasota.sensor.api.apis.SensorMicroserviceController.*(..))")
    public void logRequestMapping(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Member member;
        try {
            member = memberService.loggedMember();
        } catch (SensorException e) {
            member = null;
        }
        log.info("[API APP] [{}] Execute path {} by {}", request.getMethod(), request.getServletPath(), member == null ? "none" : member.getId());
    }
}
