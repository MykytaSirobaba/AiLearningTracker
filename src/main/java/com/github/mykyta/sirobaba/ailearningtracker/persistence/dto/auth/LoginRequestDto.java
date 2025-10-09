package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth;

import lombok.Data;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
