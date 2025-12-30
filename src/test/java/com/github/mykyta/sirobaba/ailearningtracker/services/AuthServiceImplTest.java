package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.InvalidRefreshTokenException;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.UserHasAlreadyRegistered;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.*;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.UserResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.UserMapper;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.security.totp.TotpService;
import com.github.mykyta.sirobaba.ailearningtracker.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Mykyta Sirobaba on 12.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Tag("Service")
@DisplayName("Authentication service test")
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTool jwtTool;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserMapper userMapper;
    @Mock
    private TotpService totpService;

    @InjectMocks
    private AuthServiceImpl authService;


    @Test
    @DisplayName("register: should create user and return tokens when request is valid")
    void register_ShouldReturnTokens_WhenRequestIsValid() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("test@example.com");
        request.setUsername("testuser");
        request.setPassword("password");

        User savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.USER)
                .build();

        UserResponseDto userResponseDto = UserResponseDto.builder().id(1L).email("test@example.com").build();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");
        when(userService.createUser(any(User.class))).thenReturn(savedUser);
        when(jwtTool.generateAccessToken(savedUser)).thenReturn("access-token");
        when(jwtTool.generateRefreshToken(savedUser)).thenReturn("refresh-token");
        when(jwtTool.getExpirationDate("access-token")).thenReturn(3600L);
        when(userMapper.toUserResponseDto(savedUser)).thenReturn(userResponseDto);

        TokenResponseDto result = authService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getUser()).isEqualTo(userResponseDto);

        verify(userService).createUser(argThat(user ->
                user.getEmail().equals("test@example.com") &&
                user.getPassword().equals("encodedPass") &&
                user.getRole() == Role.USER
        ));
    }

    @Test
    @DisplayName("register: should propagate exception when user already exists")
    void register_ShouldThrowException_WhenUserExists() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("existing@example.com");
        request.setPassword("pass");

        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userService.createUser(any())).thenThrow(new UserHasAlreadyRegistered("User exists"));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserHasAlreadyRegistered.class)
                .hasMessage("User exists");
    }


    @Test
    @DisplayName("login: should return tokens when credentials are valid and 2FA is disabled")
    void login_ShouldReturnTokens_When2FaDisabled() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password");
        User user = User.builder().id(1L).email("test@example.com").twoFactorEnabled(false).build();
        UserResponseDto userDto = UserResponseDto.builder().id(1L).build();

        when(userService.findByEmail(request.getEmail())).thenReturn(user);
        when(jwtTool.generateAccessToken(user)).thenReturn("access-token");
        when(jwtTool.generateRefreshToken(user)).thenReturn("refresh-token");
        when(userMapper.toUserResponseDto(user)).thenReturn(userDto);

        LoginResultDto result = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(result.isTwoFactorRequired()).isFalse();
        assertThat(result.getTokens().getAccessToken()).isEqualTo("access-token");
        assertThat(result.getTwoFactorToken()).isNull();
    }

    @Test
    @DisplayName("login: should return 2FA token when credentials are valid and 2FA is enabled")
    void login_ShouldReturn2FaToken_When2FaEnabled() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("secure@example.com");
        request.setPassword("password");
        User user = User.builder().id(2L).email("secure@example.com").twoFactorEnabled(true).build();

        when(userService.findByEmail(request.getEmail())).thenReturn(user);
        when(jwtTool.generate2FaToken(user)).thenReturn("temp-2fa-token");

        LoginResultDto result = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(result.isTwoFactorRequired()).isTrue();
        assertThat(result.getTwoFactorToken()).isEqualTo("temp-2fa-token");
        assertThat(result.getTokens()).isNull();
    }

    @Test
    @DisplayName("login: should throw BadCredentialsException when auth manager fails")
    void login_ShouldThrowException_WhenCredentialsInvalid() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("wrong@example.com");
        request.setPassword("wrong");
        doThrow(new BadCredentialsException("Bad creds"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(userService, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("completeTwoFactorLogin: should return tokens when code is valid")
    void completeTwoFactorLogin_ShouldReturnTokens_WhenCodeValid() {
        TwoFactorVerificationRequestDto request = new TwoFactorVerificationRequestDto();
        request.setTwoFactorToken("valid-2fa-token");
        request.setCode("123456");

        User user = User.builder().id(1L).twoFactorSecret("secret").build();
        UserResponseDto userDto = UserResponseDto.builder().id(1L).build();

        when(jwtTool.getUserIdFrom2FaToken("valid-2fa-token")).thenReturn(1L);
        when(userService.findById(1L)).thenReturn(user);
        when(totpService.validateCode("secret", "123456")).thenReturn(true);

        when(jwtTool.generateAccessToken(user)).thenReturn("access");
        when(jwtTool.generateRefreshToken(user)).thenReturn("refresh");
        when(userMapper.toUserResponseDto(user)).thenReturn(userDto);

        TokenResponseDto result = authService.completeTwoFactorLogin(request);

        assertThat(result.getAccessToken()).isEqualTo("access");
    }

    @Test
    @DisplayName("completeTwoFactorLogin: should throw exception when code is invalid")
    void completeTwoFactorLogin_ShouldThrowException_WhenCodeInvalid() {
        TwoFactorVerificationRequestDto request = new TwoFactorVerificationRequestDto();
        request.setTwoFactorToken("valid-token");
        request.setCode("000000");

        User user = User.builder().id(1L).twoFactorSecret("secret").build();

        when(jwtTool.getUserIdFrom2FaToken("valid-token")).thenReturn(1L);
        when(userService.findById(1L)).thenReturn(user);
        when(totpService.validateCode("secret", "000000")).thenReturn(false);

        assertThatThrownBy(() -> authService.completeTwoFactorLogin(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage(ErrorMessage.INVALID_2FA_CODE);
    }

    @Test
    @DisplayName("refresh: should return new access token when refresh token is valid")
    void refresh_ShouldReturnNewToken_WhenTokenValid() {
        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("valid-refresh");

        User user = User.builder().email("test@example.com").build();

        when(jwtTool.extractEmail("valid-refresh")).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(jwtTool.validateRefreshToken("valid-refresh", user)).thenReturn(true);
        when(jwtTool.generateAccessToken(user)).thenReturn("new-access");
        when(jwtTool.getExpirationDate("valid-refresh")).thenReturn(3600L);

        RefreshTokenResponseDto result = authService.refresh(request);

        assertThat(result.getAccessToken()).isEqualTo("new-access");
        assertThat(result.getRefreshToken()).isEqualTo("valid-refresh");
    }

    @Test
    @DisplayName("refresh: should throw exception when refresh token is invalid")
    void refresh_ShouldThrowException_WhenTokenInvalid() {
        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        String invalidToken = "invalid-refresh";
        request.setRefreshToken(invalidToken);

        User user = User.builder().email("test@example.com").build();

        when(jwtTool.extractEmail(invalidToken)).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(jwtTool.validateRefreshToken(invalidToken, user)).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessage(String.format(ErrorMessage.INVALID_REFRESH_TOKEN, invalidToken));
    }
}