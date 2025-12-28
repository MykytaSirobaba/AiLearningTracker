package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.github.mykyta.sirobaba.ailearningtracker.annotations.CurrentUser;
import com.github.mykyta.sirobaba.ailearningtracker.constants.HttpStatuses;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.TwoFactorActivationRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.TwoFactorSetupResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Tag(name = "User", description = "Endpoints for managing current user profile and security settings")
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get current user information",
            description = "Returns information about the currently authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = HttpStatuses.OK,
                            content = @Content(schema = @Schema(implementation = CurrentUserInfoDto.class))
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
            }
    )
    @GetMapping("/me")
    public ResponseEntity<CurrentUserInfoDto> getCurrentUser(
            @CurrentUser CurrentUserInfoDto currentUser
    ) {
        return ResponseEntity.ok(currentUser);
    }

    @Operation(
            summary = "Setup two-factor authentication",
            description = "Generates and returns data required to configure two-factor authentication for the current user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = HttpStatuses.OK,
                            content = @Content(schema = @Schema(implementation = TwoFactorSetupResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
            }
    )
    @GetMapping("/2fa/setup")
    public ResponseEntity<TwoFactorSetupResponseDto> setupTwoFactor(
            @CurrentUser CurrentUserInfoDto currentUser
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.setupTwoFactor(currentUser.getId()));
    }

    @Operation(
            summary = "Activate two-factor authentication",
            description = "Activates two-factor authentication for the current user after verifying the provided code.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Two-factor authentication activation data",
                    content = @Content(schema = @Schema(implementation = TwoFactorActivationRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
                    @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
            }
    )
    @PatchMapping("/2fa/activate")
    public ResponseEntity<Void> activateTwoFactor(
            @RequestBody TwoFactorActivationRequestDto request,
            @CurrentUser CurrentUserInfoDto currentUser
    ) {
        userService.activateTwoFactor(request, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Disable two-factor authentication",
            description = "Disables two-factor authentication for the current user after verifying the provided code.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Two-factor authentication verification data",
                    content = @Content(schema = @Schema(implementation = TwoFactorActivationRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
                    @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
            }
    )
    @PatchMapping("/2fa/disable")
    public ResponseEntity<Void> disableTwoFactor(
            @RequestBody TwoFactorActivationRequestDto request,
            @CurrentUser CurrentUserInfoDto currentUser
    ) {
        userService.disable2Fa(request, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}
