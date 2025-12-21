package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.github.mykyta.sirobaba.ailearningtracker.annotations.CurrentUser;
import com.github.mykyta.sirobaba.ailearningtracker.constants.HttpStatuses;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.services.SubgoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Mykyta Sirobaba on 28.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@RestController
@RequestMapping("/subgoal")
@AllArgsConstructor
public class SubgoalController {
    private final SubgoalService subgoalService;

    @Operation(
            summary = "Mark subgoal as completed",
            description = "Marks a specific subgoal as completed for the authenticated user and returns updated subgoal information.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = HttpStatuses.OK,
                            content = @Content(schema = @Schema(implementation = SubGoalResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            }
    )
    @PatchMapping("/{subgoalId}")
    public ResponseEntity<SubGoalResponseDto> completeSubgoal(@PathVariable Long subgoalId,
                                                              @CurrentUser CurrentUserInfoDto user) {
        return ResponseEntity.status(HttpStatus.OK).body(subgoalService.completeSubgoal(subgoalId, user.getId()));
    }

}
