package com.github.mykyta.sirobaba.ailearningtracker.controller.auth;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.LoginRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.RegisterRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.TokenResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by Mykyta Sirobaba on 20.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> register(@RequestBody RegisterRequestDto  registerRequestDto) {
        return ResponseEntity.ok(authService.register(registerRequestDto));
    }

    @GetMapping("/oauth2/code/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
}
