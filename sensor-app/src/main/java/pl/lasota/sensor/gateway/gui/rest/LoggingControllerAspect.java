package pl.lasota.sensor.gateway.gui.rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.lasota.sensor.exceptions.SensorException;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.member.MemberService;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingControllerAspect {

    private final MemberService memberService;

    @Before("execution(* pl.lasota.sensor.gateway.rest.api..*(..))")
    public void logRequestMapping(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Member member;
        try {
            member = memberService.loggedMember();
        } catch (SensorException e) {
            member = null;
        }
        log.info("[GUI APP] [{}] Execute path {} by {}", request.getMethod(), request.getServletPath(), member == null ? "none" : member.getId());
    }
}
