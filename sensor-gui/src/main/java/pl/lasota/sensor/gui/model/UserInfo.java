package pl.lasota.sensor.gui.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
//email is username
public class UserInfo {
    private final String fullName;
    private final Long id;
    private final List<String> roles;
}
