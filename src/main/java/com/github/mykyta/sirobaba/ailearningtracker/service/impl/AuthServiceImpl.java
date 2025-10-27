package com.github.mykyta.sirobaba.ailearningtracker.service.impl;

import com.github.mykyta.sirobaba.ailearningtracker.constant.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.exception.exceptions.UserIsAlreadyRegistered;
import com.github.mykyta.sirobaba.ailearningtracker.exception.exceptions.UserNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.exception.exceptions.UseremailNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.LoginRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.RegisterRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.TokenResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.UserMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.UserRepo;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.service.AuthService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public TokenResponseDto register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserIsAlreadyRegistered(ErrorMessage.EMAIL_ALREADY_REGISTERED);
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
                .orElseThrow(() -> new UserNotFoundException
                        (String.format(ErrorMessage.USER_WITH_THIS_EMAIL_NOT_FOUND, request.getEmail())));

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
