package com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.UserResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import org.mapstruct.Mapper;

/**
 * Mapper for converting {@link User} entities into various user-related DTOs.
 * <p>
 * This mapper is responsible only for transforming User domain objects into
 * DTOs used in authentication, authorization and profile-related responses.
 * Implemented via MapStruct with Spring component model.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a {@link User} entity to {@link UserResponseDto}.
     *
     * @param user the User entity to convert
     * @return the mapped UserResponseDto
     */
    UserResponseDto toUserResponseDto(User user);

    /**
     * Converts a {@link User} entity to {@link CurrentUserInfoDto}, which contains
     * a short summary of the currently authenticated user's identity.
     *
     * @param user the User entity to convert
     * @return the mapped CurrentUserInfoDto
     */
    CurrentUserInfoDto toCurrentUserInfoDto(User user);
}
