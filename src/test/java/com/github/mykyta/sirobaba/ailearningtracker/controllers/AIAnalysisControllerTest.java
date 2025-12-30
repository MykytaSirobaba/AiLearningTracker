package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mykyta.sirobaba.ailearningtracker.configs.WebMvcConfig;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisDetailsDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.resolvers.UserArgumentResolver;
import com.github.mykyta.sirobaba.ailearningtracker.security.CustomUserDetailsService;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.services.AIAnalysisService;
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
@AutoConfigureMockMvc
@WebMvcTest(AIAnalysisController.class)
@Import({WebMvcConfig.class, UserArgumentResolver.class})
class AIAnalysisControllerTest {

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private JwtTool jwtTool;
    @MockitoBean
    private AIAnalysisService aiAnalysisService;
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
    @DisplayName("POST /aiAnalysis/{goalId}/analysis: Should return 201 Created and analysis details")
    void createAIAnalysisShouldReturnCreatedWhenRequestIsValid() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        AIAnalysisRequestDto requestDto = AIAnalysisRequestDto.builder().limit(1).build();

        AIAnalysisDetailsDto responseDto = AIAnalysisDetailsDto.builder()
                .id(10L)
                .title("Keep going!")
                .analysisText("Analysis complete")
                .createdAt(LocalDateTime.of(2025, 12, 30, 10, 0, 0))
                .build();

        when(aiAnalysisService.createAIAnalysis(eq(currentUserInfoDto.getId()), any(AIAnalysisRequestDto.class), eq(100L)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/aiAnalysis/{goalId}/analysis", 100L)
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.analysisText").value("Analysis complete"))
                .andExpect(jsonPath("$.title").value("Keep going!"))
                .andExpect(jsonPath("$.createdAt").value("2025-12-30T10:00:00"));

        verify(aiAnalysisService).createAIAnalysis(eq(1L), any(AIAnalysisRequestDto.class), eq(100L));
    }

    @Test
    @DisplayName("GET /aiAnalysis/{goalId}/analysis/{analysisId}: Should return 200 OK and details")
    void getAIAnalysisShouldReturnDetailsWhenFound() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        AIAnalysisDetailsDto responseDto = AIAnalysisDetailsDto.builder()
                .id(10L)
                .title("Try harder")
                .analysisText("Detailed Analysis")
                .build();

        when(aiAnalysisService.getAIAnalysis(currentUserInfoDto.getId(), 10L, 100L))
                .thenReturn(responseDto);

        mockMvc.perform(get("/aiAnalysis/{goalId}/analysis/{analysisId}", 100L, 10L)
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.analysisText").value("Detailed Analysis"));

        verify(aiAnalysisService).getAIAnalysis(1L, 10L, 100L);
    }

    @Test
    @DisplayName("GET /aiAnalysis/{goalId}/analysis/: Should return 200 OK and paginated list")
    void getAIAnalysesShouldReturnPaginatedList() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        AIAnalysisResponseDto summaryDto = AIAnalysisResponseDto.builder()
                .id(5L)
                .title("Title")
                .createdAt(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();

        PageResponse<AIAnalysisResponseDto> pageResponse = new PageResponse<>(
                List.of(summaryDto),
                0,
                1,
                1L,
                1,
                true
        );

        when(aiAnalysisService.getAIAnalyses(eq(currentUserInfoDto.getId()), eq(100L), any(Pageable.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/aiAnalysis/{goalId}/analysis/", 100L)
                        .param("page", "0")
                        .param("size", "5")
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(5L))
                .andExpect(jsonPath("$.content[0].title").value("Title"))
                .andExpect(jsonPath("$.totalElements").value(1L));

        verify(aiAnalysisService).getAIAnalyses(eq(1L), eq(100L), any(Pageable.class));
    }

    @Test
    @DisplayName("DELETE /aiAnalysis/{goalId}/analysis/{analysisId}: Should return 204 No Content")
    void deleteAIAnalysisShouldReturnNoContent() throws Exception {
        when(userService.findCurrentUserInfoDto(anyString())).thenReturn(currentUserInfoDto);

        doNothing().when(aiAnalysisService).deleteAIAnalysis(1L, 100L, 10L);

        mockMvc.perform(delete("/aiAnalysis/{goalId}/analysis/{analysisId}", 100L, 10L)
                        .with(authentication(authenticationWithUser(currentUserInfoDto)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(aiAnalysisService).deleteAIAnalysis(1L, 100L, 10L);
    }
}
