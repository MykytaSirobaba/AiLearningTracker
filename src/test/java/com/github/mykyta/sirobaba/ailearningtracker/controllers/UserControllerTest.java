package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mykyta.sirobaba.ailearningtracker.configs.WebMvcConfig;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.TwoFactorActivationRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.TwoFactorSetupResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.resolvers.UserArgumentResolver;
import com.github.mykyta.sirobaba.ailearningtracker.security.CustomUserDetailsService;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.mykyta.sirobaba.ailearningtracker.SecurityTestUtils.authenticationWithUser;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Mykyta Sirobaba on 30.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
@Import({WebMvcConfig.class, UserArgumentResolver.class})
class UserControllerTest {

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private JwtTool jwtTool;
    @MockitoBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private CurrentUserInfoDto currentUserInfoDto;

    @BeforeEach
    void setup() {
        currentUserInfoDto = CurrentUserInfoDto.builder()
                .id(1L)
                .username("Jack")
                .email("test@example.com")
                .twoFactorEnabled(false)
                .role(Role.USER)
                .build();


    }

    @Test
    @DisplayName("GET /user/me returns current user info")
    void getCurrentUserShouldReturnUserInfoWhenAuthorized() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        mockMvc.perform(get("/user/me")
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Jack"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.twoFactorEnabled").value(false))
                .andExpect(jsonPath("$.role").value(Role.USER.name()));
    }

    @Test
    @DisplayName("GET /user/2fa/setup returns 2FA setup data ")
    void setupTwoFactorShouldReturnOkWhenRequestIsValid() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        TwoFactorSetupResponseDto twoFactorSetupResponseDto = TwoFactorSetupResponseDto.builder()
                .secretKey("SECRET")
                .qrCodeImageUrl("QR_CODE")
                .build();

        when(userService.setupTwoFactor(currentUserInfoDto.getId())).thenReturn(twoFactorSetupResponseDto);

        mockMvc.perform(get("/user/2fa/setup")
                        .with(authentication(authenticationWithUser(currentUserInfoDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qrCodeImageUrl").value("QR_CODE"));

        verify(userService).setupTwoFactor(1L);
    }

    @Test
    @DisplayName("PATCH /user/2fa/activate Activate 2FA: Should return 200 OK when request contains valid code")
    void activateTwoFactorShouldReturnOkWhenRequestIsValid() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        TwoFactorActivationRequestDto twoFactorActivationRequestDto = new TwoFactorActivationRequestDto();
        twoFactorActivationRequestDto.setCode("CODE");

        doNothing().when(userService).activateTwoFactor(twoFactorActivationRequestDto, currentUserInfoDto.getId());

        mockMvc.perform(patch("/user/2fa/activate")
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(twoFactorActivationRequestDto)))
                .andExpect(status().isOk());

        verify(userService).activateTwoFactor(twoFactorActivationRequestDto, currentUserInfoDto.getId());
    }

    @Test
    @DisplayName("PATCH /user/2fa/disable Disable 2FA: Should return 200 OK when verification code is valid")
    void disableTwoFactorShouldReturnOkWhenRequestIsValid() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        TwoFactorActivationRequestDto twoFactorActivationRequestDto = new TwoFactorActivationRequestDto();
        twoFactorActivationRequestDto.setCode("CODE");

        doNothing().when(userService).disable2Fa(twoFactorActivationRequestDto, currentUserInfoDto.getId());

        mockMvc.perform(patch("/user/2fa/disable")
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(twoFactorActivationRequestDto)))
                .andExpect(status().isOk());

        verify(userService).disable2Fa(twoFactorActivationRequestDto, currentUserInfoDto.getId());
    }
}
