package com.github.mykyta.sirobaba.ailearningtracker.security.oauth2;

import com.github.mykyta.sirobaba.ailearningtracker.exception.exceptions.UserNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.UserRepo;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepo userRepo;
    private final JwtTool jwtTool;
    private static final Integer COOKIES_EXPIRE = 3600;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        final String email = oAuth2User.getAttribute("email");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found after OAuth2 login"));
        String jwt = jwtTool.generateAccessToken(user);

        Cookie jwtCookie = new Cookie("jwt", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(COOKIES_EXPIRE);
        jwtCookie.setAttribute("SameSite", "none");

        response.addCookie(jwtCookie);

        response.sendRedirect("http://localhost:8080/home");
    }
}
