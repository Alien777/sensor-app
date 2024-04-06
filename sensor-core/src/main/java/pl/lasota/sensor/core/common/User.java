package pl.lasota.sensor.core.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.lasota.sensor.core.entities.Role;

import java.util.Collection;

@Builder
@Getter
public class User implements UserDetails {
    
    private final Collection<Role> roles;
    private final String id;
    private final boolean isEnabled;

    public Collection<? extends GrantedAuthority> getGrantedRoles() {
        return getRoles().stream().map(SimpleGrantedAuthority::new).toList();
    }

    public Collection<String> getRoles() {
        return roles.stream().map(Enum::name).toList();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedRoles();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }


}
