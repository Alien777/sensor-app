package pl.lasota.sensor.security.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.configs.properties.GuiProperties;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.entities.Provider;
import pl.lasota.sensor.exceptions.AuthException;
import pl.lasota.sensor.exceptions.SensorException;
import pl.lasota.sensor.exceptions.SensorMemberException;
import pl.lasota.sensor.member.MemberServiceInterface;
import pl.lasota.sensor.security.model.GoogleUserInfo;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SensorAuthenticationSuccessOAuthHandler implements AuthenticationSuccessHandler {

    private final MemberServiceInterface memberService;
    private final AuthService authService;
    private final GuiProperties properties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
        DefaultOidcUser principal = (DefaultOidcUser) auth.getPrincipal();
        GoogleUserInfo googleUserInfo = new GoogleUserInfo(principal.getAttributes());

        try {
            createUser(googleUserInfo);
        } catch (SensorMemberException e) {
            throw new AuthException("Problem with create user ", e);
        }

        try {
            Member member = memberService.findByEmailAndProvider(googleUserInfo.getEmail(), Provider.GOOGLE);
            authService.createSession(member);
            response.sendRedirect(properties.getRedirect()
                    .loginSuccess());
        } catch (SensorException e) {
            throw new AuthException("Problem with find user ", e);
        }

    }

    private void createUser(GoogleUserInfo googleUserInfo) throws SensorMemberException {
        try {
            memberService.createGoogle(googleUserInfo.getFullName(), googleUserInfo.getEmail());
        } catch (SensorException e) {
            //none, cause if user exit only loading
        }
    }
}
