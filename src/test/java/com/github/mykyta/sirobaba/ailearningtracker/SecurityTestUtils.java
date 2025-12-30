package com.github.mykyta.sirobaba.ailearningtracker;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Created by Mykyta Sirobaba on 30.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
public final class SecurityTestUtils {

    private SecurityTestUtils() {
    }

    public static Authentication authenticationWithUser(CurrentUserInfoDto user) {
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of()
        );
    }
}
