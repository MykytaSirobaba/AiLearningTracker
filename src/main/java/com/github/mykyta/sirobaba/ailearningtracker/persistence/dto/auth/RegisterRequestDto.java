package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
public class RegisterRequestDto {
    private String username;

    @Email(message = "Invalid email format", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @ToString.Exclude
    @NotEmpty(message = "Password cannot be empty")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters, include 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character")
    private String password;
}
