package com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.UserResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import org.mapstruct.Mapper;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);
}
