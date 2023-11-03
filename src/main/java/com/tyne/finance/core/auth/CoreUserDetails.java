package com.tyne.finance.core.auth;

import com.tyne.finance.core.models.Permission;
import com.tyne.finance.core.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@RequiredArgsConstructor
public class CoreUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Permission permission: this.user.getAllPermissions()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + permission.getCodename().toUpperCase()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.user.getDbPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.user.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.user.getPassword() != null;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isActive();
    }

    @Override
    public String toString() {
        return "CoreUserDetails{" + "user=" + user.getUsername() + '}';
    }
}
