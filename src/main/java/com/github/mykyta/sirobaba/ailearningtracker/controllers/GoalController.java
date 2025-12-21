package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.github.mykyta.sirobaba.ailearningtracker.annotations.CurrentUser;
import com.github.mykyta.sirobaba.ailearningtracker.constants.HttpStatuses;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalSummaryDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.services.GoalService;
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
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Tag(name = "Goals", description = "Endpoints for managing user goals and their progress")
@RestController
@RequestMapping("/goal")
@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @Operation(
            summary = "Create a new goal",
            description = "Creates a new goal for the authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Goal details for creation",
                    content = @Content(schema = @Schema(implementation = GoalRequestDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = HttpStatuses.CREATED,
                            content = @Content(schema = @Schema(implementation = GoalResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            }
    )
    @PostMapping("/create")
    public ResponseEntity<GoalResponseDto> createGoal(@RequestBody GoalRequestDto goalRequest,
                                                      @CurrentUser CurrentUserInfoDto user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.createGoal(goalRequest, user));
    }

    @Operation(
            summary = "Get goal by ID",
            description = "Returns full information about a specific goal of the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Goal found",
                            content = @Content(schema = @Schema(implementation = GoalResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            }
    )
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponseDto> getGoal(@PathVariable Long goalId, @CurrentUser CurrentUserInfoDto user){
        return ResponseEntity.status(HttpStatus.OK).body(goalService.getGoal(goalId, user.getId()));
    }

    @Operation(
            summary = "Delete a goal",
            description = "Deletes a goal by its ID if it belongs to the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "204", description = HttpStatuses.NO_CONTENT),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            }
    )
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId, @CurrentUser CurrentUserInfoDto user) {
        goalService.removeGoal(goalId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Mark goal as completed",
            description = "Marks a goal as completed and returns its updated information.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Goal successfully marked as completed",
                            content = @Content(schema = @Schema(implementation = GoalResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            }
    )
    @PatchMapping("/{goalId}/completed")
    public ResponseEntity<GoalResponseDto> completeGoal(@PathVariable Long goalId, @CurrentUser CurrentUserInfoDto user){
        return ResponseEntity.status(HttpStatus.OK).body(goalService.completeGoal(goalId, user.getId()));
    }

    @Operation(
            summary = "Get all goals",
            description = "Returns a paginated list of all active goals for the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Goals successfully retrieved",
                            content = @Content(schema = @Schema(implementation = PageResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            }
    )
    @GetMapping("/goals")
    public ResponseEntity<PageResponse<GoalSummaryDto>> getAllGoals(@Parameter(hidden = true) Pageable pageable,
                                                                    @CurrentUser CurrentUserInfoDto user){
        return ResponseEntity.status(HttpStatus.OK).body(goalService.getAllGoals(pageable, user.getId()));
    }

    @Operation(
            summary = "Get all completed goals",
            description = "Returns a paginated list of all completed goals for the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Completed goals successfully retrieved",
                            content = @Content(schema = @Schema(implementation = PageResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            }
    )
    @GetMapping("/goals/completed")
    public ResponseEntity<PageResponse<GoalSummaryDto>> getAllCompletedGoals(@Parameter(hidden = true) Pageable pageable,
                                                                             @CurrentUser CurrentUserInfoDto user){
        return ResponseEntity.status(HttpStatus.OK).body(goalService.getAllCompletedGoals(pageable, user.getId()));
    }
}
