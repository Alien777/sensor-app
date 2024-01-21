package pl.lasota.sensor.gui.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.core.exceptions.SaveMemberException;
import pl.lasota.sensor.core.exceptions.UserExistingException;
import pl.lasota.sensor.core.exceptions.UserNotExistingException;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.Provider;
import pl.lasota.sensor.core.service.MemberService;
import pl.lasota.sensor.gui.auth.model.GoogleUserInfo;
import pl.lasota.sensor.gui.config.properties.SensorProperties;
import pl.lasota.sensor.gui.exceptions.AuthException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SensorAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;
    private final AuthService authService;
    private final SensorProperties properties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
        DefaultOidcUser principal = (DefaultOidcUser) auth.getPrincipal();
        GoogleUserInfo googleUserInfo = new GoogleUserInfo(principal.getAttributes());

        try {
            createUser(googleUserInfo);
        } catch (SaveMemberException e) {
            throw new AuthException("Problem with create user ", e);
        }

        try {
            Member member = memberService.findByEmailAndProvider(googleUserInfo.getEmail(), Provider.GOOGLE);
            authService.createSession(member);
            response.sendRedirect(properties.getRedirect()
                    .loginSuccess());
        } catch (UserNotExistingException e) {
            throw new AuthException("Problem with find user ", e);
        }

    }

    private void createUser(GoogleUserInfo googleUserInfo) throws SaveMemberException {
        try {
            memberService.createGoogle(googleUserInfo.getFullName(), googleUserInfo.getEmail());
        } catch (UserExistingException e) {
            //none, cause if user exit only loading
        }
    }
}
