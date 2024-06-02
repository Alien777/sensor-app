package pl.lasota.sensor.gateway.gui.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.lasota.sensor.gateway.gui.model.UserInfo;
import pl.lasota.sensor.security.AuthService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/token")
    public ResponseEntity<String> token(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.getToken());
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
