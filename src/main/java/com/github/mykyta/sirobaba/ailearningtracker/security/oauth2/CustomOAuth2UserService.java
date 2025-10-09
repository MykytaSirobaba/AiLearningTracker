package com.github.mykyta.sirobaba.ailearningtracker.security.oauth2;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepo userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        final String provider = userRequest.getClientRegistration().getRegistrationId();
        final String providerId = oAuth2User.getAttribute("sub");
        final String email = oAuth2User.getAttribute("email");
        final String name = oAuth2User.getAttribute("name");

        User user = userRepo.findByEmail(email)
                .orElseGet(() -> userRepo.save(User.builder()
                        .username(name)
                        .email(email)
                        .password(UUID.randomUUID().toString())
                        .provider(provider.toUpperCase())
                        .providerId(providerId)
                        .role(Role.USER)
                        .refreshTokenKey(UUID.randomUUID().toString())
                        .build()));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                oAuth2User.getAttributes(),
                "sub"
        );
    }
}
