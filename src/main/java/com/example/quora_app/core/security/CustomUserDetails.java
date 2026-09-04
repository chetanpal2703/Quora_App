package com.example.quora_app.core.security;

import com.example.quora_app.feature.authorization.entity.Permission;
import com.example.quora_app.feature.authorization.entity.Role;
import com.example.quora_app.feature.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
public class CustomUserDetails implements UserDetails {
    private final UUID userId;
    private final String email;
    private final String password;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = buildAuthorities(user);
    }


    private Set<GrantedAuthority> buildAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            // Role becomes ROLE_ADMIN / ROLE_USER / ROLE_MODERATOR
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role.getName())
            );
            // Role's permissions become normal authorities
            for (Permission permission : role.getPermissions()) {
                authorities.add(
                        new SimpleGrantedAuthority(permission.getName())
                );
            }
        }
        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return true;
    }
}
