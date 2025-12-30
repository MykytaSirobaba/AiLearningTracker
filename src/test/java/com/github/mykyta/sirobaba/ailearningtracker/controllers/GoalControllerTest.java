package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mykyta.sirobaba.ailearningtracker.configs.WebMvcConfig;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalSummaryDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.resolvers.UserArgumentResolver;
import com.github.mykyta.sirobaba.ailearningtracker.security.CustomUserDetailsService;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.services.GoalService;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.github.mykyta.sirobaba.ailearningtracker.SecurityTestUtils.authenticationWithUser;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Mykyta Sirobaba on 30.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
@AutoConfigureMockMvc
@WebMvcTest(GoalController.class)
@Import({WebMvcConfig.class, UserArgumentResolver.class})
class GoalControllerTest {

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private JwtTool jwtTool;
    @MockitoBean
    private GoalService goalService;
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("POST /goal/create: Should return 201 Created when request is valid")
    void createGoalShouldReturnCreatedWhenRequestIsValid() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        GoalRequestDto requestDto = GoalRequestDto.builder()
                .title("Learn Java")
                .prompt("Master Collections and Streams")
                .deadline(LocalDate.of(2026, 1, 1))
                .hoursPerWeek(40)
                .build();

        GoalResponseDto responseDto = GoalResponseDto.builder()
                .id(100L)
                .title("Learn Java")
                .description("Master Collections and Streams")
                .difficulty(Difficulty.HARD)
                .estimatedHours(40)
                .deadline(LocalDate.of(2026, 1, 1))
                .build();

        when(goalService.createGoal(any(GoalRequestDto.class), eq(currentUserInfoDto)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/goal/create")
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.title").value("Learn Java"))
                .andExpect(jsonPath("$.difficulty").value("HARD"))
                .andExpect(jsonPath("$.deadline").value("2026-01-01"));

        verify(goalService).createGoal(any(GoalRequestDto.class), eq(currentUserInfoDto));
    }

    @Test
    @DisplayName("GET /goal/{goalId}: Should return 200 OK and goal details")
    void getGoalShouldReturnGoalWhenFound() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        GoalResponseDto responseDto = GoalResponseDto.builder()
                .id(100L)
                .title("Existing Goal")
                .build();

        when(goalService.getGoal(100L, currentUserInfoDto.getId())).thenReturn(responseDto);

        mockMvc.perform(get("/goal/{goalId}", 100L)
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.title").value("Existing Goal"));

        verify(goalService).getGoal(100L, 1L);
    }

    @Test
    @DisplayName("DELETE /goal/{goalId}: Should return 204 No Content")
    void deleteGoalShouldReturnNoContent() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        mockMvc.perform(delete("/goal/{goalId}", 100L)
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(goalService).removeGoal(100L, currentUserInfoDto.getId());
    }

    @Test
    @DisplayName("PATCH /goal/{goalId}/completed: Should return 200 OK and updated goal")
    void completeGoalShouldReturnUpdatedGoal() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        GoalResponseDto responseDto = GoalResponseDto.builder()
                .id(100L)
                .title("Completed Goal")
                .completed(true)
                .build();

        when(goalService.completeGoal(100L, currentUserInfoDto.getId())).thenReturn(responseDto);

        mockMvc.perform(patch("/goal/{goalId}/completed", 100L)
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.completed").value(true));

        verify(goalService).completeGoal(100L, 1L);
    }

    @Test
    @DisplayName("GET /goal/goals: Should return 200 OK and paginated list of active goals")
    void getAllGoalsShouldReturnPaginatedList() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        GoalSummaryDto summaryDto = GoalSummaryDto.builder()
                .id(10L)
                .title("Active Goal 1")
                .build();

        PageResponse<GoalSummaryDto> pageResponse =
                new PageResponse<>(
                        List.of(summaryDto),
                        0,
                        1,
                        1L,
                        1,
                        true
                );

        when(goalService.getAllGoals(any(Pageable.class), eq(currentUserInfoDto.getId())))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/goal/goals")
                        .param("page", "0")
                        .param("size", "5")
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10L))
                .andExpect(jsonPath("$.content[0].title").value("Active Goal 1"))
                .andExpect(jsonPath("$.totalElements").value(1L));

        verify(goalService).getAllGoals(any(Pageable.class), eq(1L));
    }

    @Test
    @DisplayName("GET /goal/goals/completed: Should return 200 OK and paginated list of completed goals")
    void getAllCompletedGoalsShouldReturnPaginatedList() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        GoalSummaryDto summaryDto = GoalSummaryDto.builder()
                .id(20L)
                .title("Finished Goal")
                .build();

        PageResponse<GoalSummaryDto> pageResponse =
                new PageResponse<>(
                        List.of(summaryDto),
                        0,
                        1,
                        1L,
                        1,
                        true
                );

        when(goalService.getAllCompletedGoals(any(Pageable.class), eq(currentUserInfoDto.getId())))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/goal/goals/completed")
                        .param("page", "0")
                        .param("size", "10")
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(20L))
                .andExpect(jsonPath("$.content[0].title").value("Finished Goal"));

        verify(goalService).getAllCompletedGoals(any(Pageable.class), eq(1L));
    }
}
