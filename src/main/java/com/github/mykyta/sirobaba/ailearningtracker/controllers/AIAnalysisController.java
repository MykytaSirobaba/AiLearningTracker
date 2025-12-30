package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.github.mykyta.sirobaba.ailearningtracker.annotations.CurrentUser;
import com.github.mykyta.sirobaba.ailearningtracker.constants.HttpStatuses;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisDetailsDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.services.AIAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Mykyta Sirobaba on 30.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Tag(name = "AIAnalysis", description = "Manages AI integration for analyzing project data and generating smart recommendations.")
@RestController
@RequestMapping("/aiAnalysis")
@AllArgsConstructor
public class AIAnalysisController {
    private final AIAnalysisService aiAnalysisService;

    @Operation(
            summary = "Create AI analysis",
            description = "Triggers a new AI-powered analysis for a specific goal of the authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Data for AI analysis",
                    content = @Content(schema = @Schema(implementation = AIAnalysisRequestDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = HttpStatuses.CREATED,
                            content = @Content(schema = @Schema(implementation = AIAnalysisDetailsDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
            }
    )
    @PostMapping("/{goalId}/analysis")
    public ResponseEntity<AIAnalysisDetailsDto> createAIAnalysis(@CurrentUser CurrentUserInfoDto userInfoDto,
                                                                 @RequestBody @Valid AIAnalysisRequestDto aiAnalysisRequestDto,
                                                                 @PathVariable("goalId") Long goalId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aiAnalysisService.createAIAnalysis(userInfoDto.getId(), aiAnalysisRequestDto, goalId));
    }

    @Operation(
            summary = "Get AI analysis by ID",
            description = "Returns detailed results of a specific AI analysis for the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "AI analysis found",
                            content = @Content(schema = @Schema(implementation = AIAnalysisDetailsDto.class))
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
            }
    )
    @GetMapping("/{goalId}/analysis/{analysisId}")
    public ResponseEntity<AIAnalysisDetailsDto> getAIAnalysis(@CurrentUser CurrentUserInfoDto userInfoDto,
                                                              @PathVariable("goalId") Long goalId,
                                                              @PathVariable("analysisId") Long analysisId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(aiAnalysisService.getAIAnalysis(userInfoDto.getId(), analysisId, goalId));
    }

    @Operation(
            summary = "Get all AI analyses for a goal",
            description = "Returns a paginated list of AI analyses for the specified goal.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "AI analyses retrieved",
                            content = @Content(schema = @Schema(implementation = PageResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
            }
    )
    @GetMapping("/{goalId}/analysis/")
    public ResponseEntity<PageResponse<AIAnalysisResponseDto>> getAIAnalyses(@CurrentUser CurrentUserInfoDto userInfoDto,
                                                                             @PathVariable("goalId") Long goalId,
                                                                             @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(aiAnalysisService.getAIAnalyses(userInfoDto.getId(), goalId, pageable));
    }

    @Operation(
            summary = "Delete AI analysis",
            description = "Deletes a specific AI analysis belonging to the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = HttpStatuses.NO_CONTENT
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
            }
    )
    @DeleteMapping("/{goalId}/analysis/{analysisId}")
    public ResponseEntity<Void> deleteAIAnalysis(@CurrentUser CurrentUserInfoDto userInfoDto,
                                                 @PathVariable("goalId") Long goalId,
                                                 @PathVariable("analysisId") Long analysisId) {
        aiAnalysisService.deleteAIAnalysis(userInfoDto.getId(), goalId, analysisId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
