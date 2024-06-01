package pl.lasota.sensor.security.model;

import java.util.Map;

public class GoogleUserInfo {

    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getFullName() {
        return (String) attributes.get("fullName");
    }
}