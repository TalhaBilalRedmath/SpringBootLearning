package org.redmath.Config;

import lombok.extern.slf4j.Slf4j;
import org.redmath.Model.User;
import org.redmath.Repository.ContactRep;
import org.redmath.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OAuthConfig implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ContactRep contactRep;

    public OAuthConfig() {
        System.out.println("✅ OAuthConfig constructor initialized");
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("✅ OAuthConfig LOADUSER initialized");

        // Load user from Google
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("username");

        System.out.println("Processing OAuth user: " + email);



        // Check if user exists as teacher first
        User admin = userRepo.findByEmail(email);
        String role = admin.getRole();
        if (admin != null) {
            System.out.println("✅ Admin exists: " + admin.getUsername());
            System.out.println("ROLE: " + admin.getRole());
        } else {
            System.out.println("❌ User not found in database: " + email);
            throw new OAuth2AuthenticationException("User with email " + email + " not registered");
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), "email");
    }
}