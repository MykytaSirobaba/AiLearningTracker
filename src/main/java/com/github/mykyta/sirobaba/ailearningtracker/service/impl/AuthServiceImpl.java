package com.github.mykyta.sirobaba.ailearningtracker.service.impl;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.LoginRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.RegisterRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.TokenResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.UserMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.UserRepo;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public TokenResponseDto register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .provider("LOCAL")
                .role(Role.USER)
                .refreshTokenKey(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);

        return buildTokenResponse(user);
    }

    @Override
    public TokenResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildTokenResponse(user);
    }

    private TokenResponseDto buildTokenResponse(User user) {
        String accessToken = jwtTool.generateAccessToken(user);
        String refreshToken = jwtTool.generateRefreshToken(user);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTool.getExpirationDate(accessToken))
                .user(userMapper.toUserResponseDto(user))
                .build();
    }

}
