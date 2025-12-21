package com.github.mykyta.sirobaba.ailearningtracker.controllers;

import com.github.mykyta.sirobaba.ailearningtracker.annotations.CurrentUser;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.TwoFactorActivationRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.TwoFactorSetupResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/2fa/setup")
    public ResponseEntity<TwoFactorSetupResponseDto> setupTwoFactor(@CurrentUser CurrentUserInfoDto currentUser) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.setupTwoFactor(currentUser.getId()));
    }

    @PatchMapping("/2fa/activate")
    public ResponseEntity<Void> activateTwoFactor(@RequestBody TwoFactorActivationRequestDto request,
                                                  @CurrentUser CurrentUserInfoDto currentUser) {
        userService.activateTwoFactor(request, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/2fa/disable")
    public ResponseEntity<Void> disableTwoFactor(@RequestBody TwoFactorActivationRequestDto request,
                                                 @CurrentUser CurrentUserInfoDto currentUser) {
        userService.disable2Fa(request, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}
