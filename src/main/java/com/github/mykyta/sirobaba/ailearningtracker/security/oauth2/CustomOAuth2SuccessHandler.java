package com.github.mykyta.sirobaba.ailearningtracker.security.oauth2;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.UserNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.UserRepo;
import com.github.mykyta.sirobaba.ailearningtracker.properties.FrontendProperties;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepo userRepo;
    private final JwtTool jwtTool;
    private final FrontendProperties frontendProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        final String email = oAuth2User.getAttribute("email");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_AFTER_OAUT2));

        if (user.isTwoFactorEnabled()) {
            String twoFactorToken = jwtTool.generate2FaToken(user);
            String redirectUrl = UriComponentsBuilder.fromUriString(frontendProperties.getTwoFaPageUrl())
                    .queryParam("tempToken", twoFactorToken)
                    .build().toUriString();
            response.sendRedirect(redirectUrl);
            return;
        }

        String accessToken = jwtTool.generateAccessToken(user);
        String refreshToken = jwtTool.generateRefreshToken(user);
        log.info("Access token: " + accessToken);
        log.info("Refresh token: " + refreshToken);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendProperties.getFrontendUrl())
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("username", URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8))
                .build().toUriString();

        log.info("Redirect URL: " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
