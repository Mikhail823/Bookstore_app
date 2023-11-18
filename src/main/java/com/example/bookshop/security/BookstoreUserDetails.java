package com.example.bookshop.security;

import com.example.bookshop.struct.user.RoleEntity;
import com.example.bookshop.struct.user.RoleType;
import com.example.bookshop.struct.user.UserContactEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;


public class BookstoreUserDetails implements UserDetails, OAuth2User {

    @Getter
    private final UserContactEntity contact;

    public BookstoreUserDetails(UserContactEntity contact) {
        this.contact = contact;

    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<RoleEntity> roles = contact.getUserId().getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (RoleEntity role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return contact.getUserId().getPassword();
    }

    @Override
    public String getUsername() {
        return contact.getContact();
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

    @Override
    public String getName() {
        return contact.getUserId().getName();
    }

    public String getPhone(){
        return contact.getContact();
    }
}
