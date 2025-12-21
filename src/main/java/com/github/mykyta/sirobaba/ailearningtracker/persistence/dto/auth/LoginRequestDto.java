package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
public class LoginRequestDto {
    @Email(message = "Invalid email format")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}
