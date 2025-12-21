package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.github.mykyta.sirobaba.ailearningtracker.annotations.CurrentUser;
import com.github.mykyta.sirobaba.ailearningtracker.constants.HttpStatuses;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogDetailsResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.services.ProgressLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Mykyta Sirobaba on 30.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Tag(name = "ProgressLog", description = "Handles user progress tracking")
@RestController
@RequestMapping("/progressLog")
@AllArgsConstructor
public class ProgressLogController {

    private final ProgressLogService progressLogService;

    @Operation(
            summary = "Add progress log to goal",
            description = "Creates a new progress log entry for a specific goal belonging to the authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Progress log details to add",
                    content = @Content(schema = @Schema(implementation = ProgressLogRequestDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = HttpStatuses.CREATED,
                            content = @Content(schema = @Schema(implementation = ProgressLogResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
            }
    )
    @PostMapping("/{goalId}/log")
    public ResponseEntity<ProgressLogResponseDto> addProgressLogToGoal(@PathVariable Long goalId,
                                                                       @RequestBody ProgressLogRequestDto progressLog,
                                                                       @CurrentUser CurrentUserInfoDto user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(progressLogService.createProgressLog(goalId, progressLog, user.getId()));
    }

    @Operation(
            summary = "Get all progress logs for a goal",
            description = "Returns a paginated list of progress logs for a specific goal belonging to the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Progress logs returned successfully",
                            content = @Content(schema = @Schema(implementation = PageResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
            }
    )
    @GetMapping("/{goalId}/logs")
    public ResponseEntity<PageResponse<ProgressLogResponseDto>> getLogsForGoal(@PathVariable Long goalId,
                                                                               @Parameter(hidden = true) Pageable pageable,
                                                                               @CurrentUser CurrentUserInfoDto user) {
        return ResponseEntity.status(HttpStatus.OK).body(progressLogService.getLogsForGoal(pageable, goalId, user.getId()));
    }

    @Operation(
            summary = "Get progress log details",
            description = "Returns full details of a specific progress log associated with a user's goal.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Progress log found",
                            content = @Content(schema = @Schema(implementation = ProgressLogDetailsResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
            }
    )
    @GetMapping("/{goalId}/{logId}")
    public ResponseEntity<ProgressLogDetailsResponseDto> getProgressLogDetails(@PathVariable Long goalId,
                                                                               @PathVariable Long logId,
                                                                               @CurrentUser CurrentUserInfoDto user) {
        return ResponseEntity.status(HttpStatus.OK).body(progressLogService.getProgressLogDetails(goalId, logId, user.getId()));
    }

    @Operation(
            summary = "Delete progress log",
            description = "Deletes a specific progress log belonging to the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = HttpStatuses.NO_CONTENT
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
            }
    )
    @DeleteMapping("/{goalId}/{logId}")
    public ResponseEntity<Void> deleteProgressLog(@PathVariable Long goalId,
                                                  @PathVariable Long logId,
                                                  @CurrentUser CurrentUserInfoDto user) {
        progressLogService.deleteProgressLog(goalId, logId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
