package com.github.mykyta.sirobaba.ailearningtracker.security.filters;

import com.github.mykyta.sirobaba.ailearningtracker.security.CustomUserDetailsService;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Created by Mykyta Sirobaba on 20.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Slf4j
@AllArgsConstructor
@Component
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTool jwtTool;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Authorization header not present");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring("Bearer ".length());
        final String email = jwtTool.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                if (jwtTool.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    log.debug("Authentication successful for user: {}", email);
                } else {
                    log.warn("JWT validation failed for user: {}", email);
                }
            } catch (Exception e) {
                log.warn("Error during JWT processing: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
