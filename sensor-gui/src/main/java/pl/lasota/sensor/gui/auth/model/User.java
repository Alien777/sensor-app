package pl.lasota.sensor.gui.auth.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.lasota.sensor.core.model.Role;

import java.util.Collection;

@Builder
@Getter
public class User implements UserDetails {
    
    private final Collection<Role> roles;
    private final Long id;
    private final String fullName;
    private final String username;
    private final boolean isEnabled;

    public Collection<? extends GrantedAuthority> getGrantedRoles() {
        return roles.stream().map(s->new SimpleGrantedAuthority(s.name())).toList();
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
        return username;
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
