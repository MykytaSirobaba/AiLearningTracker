package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.github.mykyta.sirobaba.ailearningtracker.configs.WebMvcConfig;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.resolvers.UserArgumentResolver;
import com.github.mykyta.sirobaba.ailearningtracker.security.CustomUserDetailsService;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.services.SubgoalService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Mykyta Sirobaba on 30.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
@AutoConfigureMockMvc
@WebMvcTest(SubgoalController.class)
@Import({WebMvcConfig.class, UserArgumentResolver.class})
class SubgoalControllerTest {

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private JwtTool jwtTool;
    @MockitoBean
    private SubgoalService subgoalService;
    @MockitoBean
    private UserService userService;
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
    @DisplayName("PATCH /user/{subgoalId} Complete Subgoal: Should return 200 OK and updated DTO when request is valid")
    void completeSubgoalShouldReturnUpdatedSubgoalWhenRequestIsValid() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        SubGoalResponseDto subGoalResponseDto = SubGoalResponseDto.builder()
                .id(1L)
                .title("Learn Spring Security")
                .description("Understand filters, JWT, and OAuth2")
                .difficulty(Difficulty.MEDIUM)
                .estimatedHours(12)
                .completed(true)
                .build();


        when(subgoalService.completeSubgoal(1L, 1L)).thenReturn(subGoalResponseDto);

        mockMvc.perform(patch("/subgoal/{subgoalId}", 1L)
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Learn Spring Security"))
                .andExpect(jsonPath("$.description").value("Understand filters, JWT, and OAuth2"))
                .andExpect(jsonPath("$.difficulty").value(Difficulty.MEDIUM.name()))
                .andExpect(jsonPath("$.estimatedHours").value(12))
                .andExpect(jsonPath("$.completed").value(true));

        verify(subgoalService).completeSubgoal(1L, 1L);
    }
}
