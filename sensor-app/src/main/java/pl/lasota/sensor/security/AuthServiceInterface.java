package pl.lasota.sensor.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.lasota.sensor.exceptions.AuthException;

import java.util.Map;


public interface AuthServiceInterface {
    String AUTHORIZATION = "Authorization";

    String getToken();

    void logout(HttpServletRequest request, HttpServletResponse response);

    void login(HttpServletRequest request, HttpServletResponse response) throws  AuthException;

    void initialAuthenticationByMemberId(String memberId) throws AuthException;

    void initialAuthenticationByOnlyToken(Map<String, String> request) throws AuthException;

    void initialAuthenticationContextByOnlySession(HttpServletRequest request) throws AuthException;

    void initiateAuthenticationContext(HttpServletRequest request) throws AuthException;
}
