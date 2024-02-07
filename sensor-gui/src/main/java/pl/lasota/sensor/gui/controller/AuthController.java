package pl.lasota.sensor.gui.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.lasota.sensor.gui.auth.AuthService;
import pl.lasota.sensor.gui.config.properties.SensorProperties;
import pl.lasota.sensor.gui.model.UserInfo;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SensorProperties sensorProperties;

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        String token = authService.getToken();
        return ResponseEntity.ok(token);
    }

    @DeleteMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
    }

    @GetMapping("/user-details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserInfo> getUserDetails() {
        return ResponseEntity.ok(authService.getUserDetails());
    }

}
