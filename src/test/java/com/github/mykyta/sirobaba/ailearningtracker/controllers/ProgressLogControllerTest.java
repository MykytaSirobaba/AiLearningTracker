package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mykyta.sirobaba.ailearningtracker.configs.WebMvcConfig;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogDetailsResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.resolvers.UserArgumentResolver;
import com.github.mykyta.sirobaba.ailearningtracker.security.CustomUserDetailsService;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.services.ProgressLogService;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.mykyta.sirobaba.ailearningtracker.SecurityTestUtils.authenticationWithUser;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Mykyta Sirobaba on 30.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Tag("Controller")
@AutoConfigureMockMvc
@WebMvcTest(ProgressLogController.class)
@DisplayName("Progress log controller test")
@Import({WebMvcConfig.class, UserArgumentResolver.class})
class ProgressLogControllerTest {

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private JwtTool jwtTool;
    @MockitoBean
    private ProgressLogService progressLogService;
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
    @DisplayName("POST /progressLog/{goalId}/log Add log: Should return 201 Created and response DTO when request is valid")
    void addProgressLogToGoalShouldReturnCreatedWhenRequestIsValid() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        ProgressLogRequestDto request = ProgressLogRequestDto.builder()
                .title("Spring MVC controller testing")
                .hours(2)
                .minutes(30)
                .note("Worked on JWT authentication, filters, and wrote controller tests.")
                .build();

        ProgressLogResponseDto response = ProgressLogResponseDto.builder()
                .progressLogId(1L)
                .title("Spring MVC controller testing")
                .logTime(LocalDateTime.of(2019, 1, 1, 0, 0, 0))
                .totalMinutes(150)
                .formattedTime("2h 30m")
                .build();

        when(progressLogService.createProgressLog(anyLong(), any(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/progressLog/{goalId}/log", 1L)
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.progressLogId").value(1L))
                .andExpect(jsonPath("$.title").value("Spring MVC controller testing"))
                .andExpect(jsonPath("$.logTime").value("2019-01-01T00:00:00"))
                .andExpect(jsonPath("$.totalMinutes").value(150))
                .andExpect(jsonPath("$.formattedTime").value("2h 30m"));

        verify(progressLogService).createProgressLog(eq(1L), any(), eq(1L));
    }

    @Test
    @DisplayName("GET /progressLog/{goalId}/logs Get all logs: Should return 200 OK and paginated list")
    void getLogsForGoalShouldReturnPaginatedListWhenRequestIsValid() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        ProgressLogResponseDto logDto = ProgressLogResponseDto.builder()
                .progressLogId(10L)
                .title("Log 1")
                .formattedTime("1h 00m")
                .build();

        PageResponse<ProgressLogResponseDto> pageResponse = new PageResponse<>(
                List.of(logDto),
                0,
                1,
                1L,
                1,
                true
        );


        when(progressLogService.getLogsForGoal(any(Pageable.class), eq(1L), eq(currentUserInfoDto.getId())))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/progressLog/{goalId}/logs", 1L)
                        .param("page", "0")
                        .param("size", "10")
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].progressLogId").value(10L))
                .andExpect(jsonPath("$.content[0].title").value("Log 1"))
                .andExpect(jsonPath("$.totalElements").value(1L));

        verify(progressLogService).getLogsForGoal(any(Pageable.class), eq(1L), eq(1L));
    }

    @Test
    @DisplayName("GET /progressLog/{goalId}/{logId} Get details: Should return 200 OK and details DTO")
    void getProgressLogDetailsShouldReturnDetailsWhenFound() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        ProgressLogDetailsResponseDto detailsDto = ProgressLogDetailsResponseDto.builder()
                .progressLogId(2L)
                .title("Detailed Log")
                .note("Very important note")
                .formattedTime("45m")
                .build();

        when(progressLogService.getProgressLogDetails(1L, 2L, currentUserInfoDto.getId()))
                .thenReturn(detailsDto);

        mockMvc.perform(get("/progressLog/{goalId}/{logId}", 1L, 2L)
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressLogId").value(2L))
                .andExpect(jsonPath("$.title").value("Detailed Log"))
                .andExpect(jsonPath("$.note").value("Very important note"));

        verify(progressLogService).getProgressLogDetails(1L, 2L, 1L);
    }

    @Test
    @DisplayName("DELETE /progressLog/{goalId}/{logId} Delete log: Should return 204 No Content")
    void deleteProgressLogShouldReturnNoContentWhenRequestIsValid() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        doNothing().when(progressLogService).deleteProgressLog(1L, 2L, 1L);

        mockMvc.perform(delete("/progressLog/{goalId}/{logId}", 1L, 2L)
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(progressLogService).deleteProgressLog(1L, 2L, 1L);
    }
}
