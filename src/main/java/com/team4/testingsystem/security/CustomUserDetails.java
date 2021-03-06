package com.team4.testingsystem.security;

import com.team4.testingsystem.entities.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String name;
    private final String login;
    private final String password;
    private final String roleName;
    private final String avatar;
    private final String language;

    private final Collection<SimpleGrantedAuthority> roles;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.roleName = user.getRole().getRoleName();
        this.avatar = user.getAvatar();
        this.language = user.getLanguage();
        this.roles = Collections.singletonList(new SimpleGrantedAuthority(this.roleName));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
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
