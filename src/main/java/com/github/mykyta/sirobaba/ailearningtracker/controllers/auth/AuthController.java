package com.github.mykyta.sirobaba.ailearningtracker.controllers.auth;

import com.github.mykyta.sirobaba.ailearningtracker.constants.HttpStatuses;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.*;
import com.github.mykyta.sirobaba.ailearningtracker.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by Mykyta Sirobaba on 20.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Login user",
            description = "Authenticates user credentials and returns a JWT access token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User credentials (email and password)",
                    content = @Content(schema = @Schema(implementation = LoginRequestDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = HttpStatuses.OK,
                            content = @Content(schema = @Schema(implementation = LoginResultDto.class))
                    ),
                    @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResultDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResultDto result = authService.login(loginRequestDto);

        if (result.isTwoFactorRequired()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/2fa/verify")
    public TokenResponseDto verifyTwoFactor(@RequestBody TwoFactorVerificationRequestDto request) {
        return authService.completeTwoFactorLogin(request);
    }

    @Operation(
            summary = "Register new user",
            description = "Registers a new user and returns a JWT access token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User registration details",
                    content = @Content(schema = @Schema(implementation = RegisterRequestDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = HttpStatuses.OK,
                            content = @Content(schema = @Schema(implementation = TokenResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
            }
    )
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        return ResponseEntity.ok(authService.register(registerRequestDto));
    }

    @Operation(
            summary = "Login via Google OAuth2",
            description = "Redirects the user to Google for authentication via OAuth2.",
            responses = {
                    @ApiResponse(responseCode = "302", description = HttpStatuses.FOUND),
                    @ApiResponse(responseCode = "500", description = HttpStatuses.INTERNAL_SERVER_ERROR)
            }
    )
    @GetMapping("/oauth2/code/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
    return ResponseEntity.ok(authService.refresh(refreshTokenRequestDto));
    }
}
