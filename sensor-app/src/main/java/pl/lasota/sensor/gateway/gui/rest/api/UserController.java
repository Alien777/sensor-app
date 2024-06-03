package pl.lasota.sensor.gateway.gui.rest.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.member.MemberService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {


    private final MemberService service;

    @GetMapping("/member-key")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getmemberId()  {
        return ResponseEntity.ok(List.of(service.loggedMember().getId()));
    }
}
