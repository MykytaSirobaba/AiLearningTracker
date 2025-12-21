package com.github.mykyta.sirobaba.ailearningtracker.services.impl;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.UserHasAlreadyRegistered;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.UserNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.*;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.UserMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.UserRepo;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.security.totp.TotpService;
import com.github.mykyta.sirobaba.ailearningtracker.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service implementation for handling user authentication and registration.
 * <p>
 * Provides functionality to register new users, login existing users,
 * and generate JWT tokens.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final TotpService totpService;

    /**
     * Registers a new user with the provided registration data.
     *
     * @param request registration request DTO containing username, email, and password
     * @return token response DTO with access and refresh tokens
     * @throws UserHasAlreadyRegistered if email or username already exists
     */
    @Override
    public TokenResponseDto register(RegisterRequestDto request) {
        log.info("Registering new user: email={}, username={}", request.getEmail(), request.getUsername());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed: email already registered: {}", request.getEmail());
            throw new UserHasAlreadyRegistered(ErrorMessage.EMAIL_ALREADY_REGISTERED);
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration failed: username already registered: {}", request.getUsername());
            throw new UserHasAlreadyRegistered(ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_NAME);
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
        log.info("User registered successfully: id={}, email={}", user.getId(), user.getEmail());

        return buildTokenResponse(user);
    }

    /**
     * Authenticates a user using email and password and returns a JWT token response.
     *
     * @param request login request DTO containing email and password
     * @return login result DTO with access and refresh tokens or 2fa token
     * @throws UserNotFoundException if the user with the given email does not exist
     */
    @Override
    public LoginResultDto login(LoginRequestDto request) {
        log.info("Attempting login for email={}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found with email={}", request.getEmail());
                    return new UserNotFoundException(String.format(
                            ErrorMessage.USER_WITH_THIS_EMAIL_NOT_FOUND,
                            request.getEmail()
                    ));
                });

        if (user.isTwoFactorEnabled()) {
            log.info("2FA enabled for user id={}, requiring second step.", user.getId());

            String twoFactorToken = jwtTool.generate2FaToken(user);

            return LoginResultDto.builder()
                    .twoFactorRequired(true)
                    .twoFactorToken(twoFactorToken)
                    .build();
        }

        log.info("Login successful (no 2FA required) for user id={}", user.getId());
        TokenResponseDto tokens = buildTokenResponse(user);

        return LoginResultDto.builder()
                .tokens(tokens)
                .twoFactorRequired(false)
                .build();
    }

    @Override
    public TokenResponseDto completeTwoFactorLogin(TwoFactorVerificationRequestDto request) {
        Long userId = jwtTool.getUserIdFrom2FaToken(request.getTwoFactorToken());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_FROM_2FA_TOKEN));

        boolean isCodeValid = totpService.validateCode(
                user.getTwoFactorSecret(),
                request.getCode()
        );

        if (!isCodeValid) {
            throw new BadCredentialsException(ErrorMessage.INVALID_2FA_CODE);
        }

        return buildTokenResponse(user);
    }

    /**
     * Builds a token response object for a given user.
     *
     * @param user the authenticated or newly registered user
     * @return token response DTO containing access token, refresh token,
     * token type, expiration, and user profile data
     */
    private TokenResponseDto buildTokenResponse(User user) {
        log.debug("Generating JWT tokens for user id={}", user.getId());

        String accessToken = jwtTool.generateAccessToken(user);
        String refreshToken = jwtTool.generateRefreshToken(user);

        TokenResponseDto response = TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTool.getExpirationDate(accessToken))
                .user(userMapper.toUserResponseDto(user))
                .build();

        log.debug("Token response built successfully for user id={}", user.getId());
        return response;
    }
}
