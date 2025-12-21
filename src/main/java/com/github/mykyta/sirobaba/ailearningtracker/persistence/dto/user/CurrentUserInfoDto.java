package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import lombok.*;

/**
 * Created by Mykyta Sirobaba on 29.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Data
@Builder
@AllArgsConstructor
public class CurrentUserInfoDto {
    private Long id;
    private String username;
    private String email;
    private Role role ;
}
