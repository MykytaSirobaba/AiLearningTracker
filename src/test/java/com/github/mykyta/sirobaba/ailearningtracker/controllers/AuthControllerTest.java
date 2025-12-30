package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mykyta.sirobaba.ailearningtracker.configs.SecurityConfig;
import com.github.mykyta.sirobaba.ailearningtracker.configs.WebMvcConfig;
import com.github.mykyta.sirobaba.ailearningtracker.controllers.auth.AuthController;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.*;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.UserResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.resolvers.UserArgumentResolver;
import com.github.mykyta.sirobaba.ailearningtracker.security.CustomUserDetailsService;
import com.github.mykyta.sirobaba.ailearningtracker.security.filters.AccessTokenAuthenticationFilter;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.security.oauth2.CustomOAuth2SuccessHandler;
import com.github.mykyta.sirobaba.ailearningtracker.security.oauth2.CustomOAuth2UserService;
import com.github.mykyta.sirobaba.ailearningtracker.services.AuthService;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Mykyta Sirobaba on 30.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
@AutoConfigureMockMvc
@WebMvcTest(AuthController.class)
@Import({WebMvcConfig.class, UserArgumentResolver.class, SecurityConfig.class})
class AuthControllerTest {

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private JwtTool jwtTool;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;
    @MockitoBean
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @MockitoBean
    private AccessTokenAuthenticationFilter accessTokenAuthenticationFilter;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws ServletException, IOException {
        doAnswer(invocation -> {
            jakarta.servlet.ServletRequest request = invocation.getArgument(0);
            jakarta.servlet.ServletResponse response = invocation.getArgument(1);
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(accessTokenAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    @DisplayName("POST /auth/login: Should return 200 OK with nested tokens when 2FA is NOT required")
    void loginShouldReturnOkWhen2FaNotRequired() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password");

        UserResponseDto userDto = UserResponseDto.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.USER.name())
                .build();

        TokenResponseDto tokenResponse = TokenResponseDto.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-123")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(userDto)
                .build();

        LoginResultDto resultDto = LoginResultDto.builder()
                .tokens(tokenResponse)
                .twoFactorRequired(false)
                .build();

        when(authService.login(any(LoginRequestDto.class))).thenReturn(resultDto);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.twoFactorRequired").value(false))
                .andExpect(jsonPath("$.tokens.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.tokens.refreshToken").value("refresh-token-123"))
                .andExpect(jsonPath("$.tokens.user.email").value("test@example.com"));

        verify(authService).login(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("POST /auth/login: Should return 202 Accepted when 2FA IS required")
    void loginShouldReturnAcceptedWhen2FaIsRequired() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail("secure@example.com");
        requestDto.setPassword("password");

        LoginResultDto resultDto = LoginResultDto.builder()
                .twoFactorToken("temp-2fa-token-xyz")
                .twoFactorRequired(true)
                .tokens(null)
                .build();

        when(authService.login(any(LoginRequestDto.class))).thenReturn(resultDto);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.twoFactorRequired").value(true))
                .andExpect(jsonPath("$.twoFactorToken").value("temp-2fa-token-xyz"));
    }

    @Test
    @DisplayName("POST /auth/2fa/verify: Should return 200 OK and TokenResponseDto")
    void verifyTwoFactorShouldReturnTokens() throws Exception {
        TwoFactorVerificationRequestDto requestDto = new TwoFactorVerificationRequestDto();
        requestDto.setTwoFactorToken("temp-2fa-token-xyz");
        requestDto.setCode("123456");

        UserResponseDto userDto = UserResponseDto.builder().id(1L).email("test@example.com").build();

        TokenResponseDto tokenResponse = TokenResponseDto.builder()
                .accessToken("access-after-2fa")
                .refreshToken("refresh-after-2fa")
                .user(userDto)
                .build();

        when(authService.completeTwoFactorLogin(any(TwoFactorVerificationRequestDto.class)))
                .thenReturn(tokenResponse);

        mockMvc.perform(post("/auth/2fa/verify")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-after-2fa"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /auth/register: Should return 200 OK and TokenResponseDto")
    void registerShouldReturnOk() throws Exception {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setEmail("new@example.com");
        requestDto.setPassword("pass");
        requestDto.setUsername("newuser");

        UserResponseDto userDto = UserResponseDto.builder().id(2L).email("new@example.com").build();

        TokenResponseDto tokenResponse = TokenResponseDto.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .user(userDto)
                .build();

        when(authService.register(any(RegisterRequestDto.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.user.id").value(2L));
    }

    @Test
    @DisplayName("GET /auth/oauth2/code/google: Should redirect")
    void googleLoginShouldRedirect() throws Exception {
        mockMvc.perform(get("/auth/oauth2/code/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/google"));
    }

    @Test
    @DisplayName("POST /auth/refresh: Should return 200 OK and new tokens")
    void refreshTokenShouldReturnNewToken() throws Exception {
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto();
        requestDto.setRefreshToken("new-refresh-token-SdcxsAXvcddSxcsdwa");

        RefreshTokenResponseDto responseDto = RefreshTokenResponseDto.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token-SdcxsAXvcddSxcsdwa")
                .build();

        when(authService.refresh(any(RefreshTokenRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }
}
