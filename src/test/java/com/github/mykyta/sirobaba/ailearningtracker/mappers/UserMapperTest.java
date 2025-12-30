package com.github.mykyta.sirobaba.ailearningtracker.mappers;

import com.github.mykyta.sirobaba.ailearningtracker.ModelUtils;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.UserResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.UserMapper;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Tag("Mapper")
@DisplayName("UserMapper Tests")
class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = ModelUtils.createTestUser();
    }

    @Nested
    @DisplayName("Method: toUserResponseDto")
    class ToUserResponseDtoTests {
        @Test
        @DisplayName("Should map all common fields correctly from User to UserResponseDto")
        void toUserResponseDto_ShouldMapAllFieldsCorrectly() {
            UserResponseDto dto = userMapper.toUserResponseDto(testUser);

            assertNotNull(dto);

            assertEquals(testUser.getId(), dto.getId(), "Id field should be mapped correctly");
            assertEquals(testUser.getUsername(), dto.getUsername(), "Username field should be mapped correctly");
            assertEquals(testUser.getEmail(), dto.getEmail(), "Email field should be mapped correctly");
        }

        @Test
        @DisplayName("Should return null when the input User is null")
        void toUserResponseDto_ShouldReturnNullWhenUserIsNull() {
            assertNull(userMapper.toUserResponseDto(null));
        }
    }

    @Nested
    @DisplayName("Method: toCurrentUserInfoDto")
    class ToCurrentUserInfoDtoTests {
        @Test
        @DisplayName("Should map specific fields correctly from User to CurrentUserInfoDto")
        void toCurrentUserInfoDto_ShouldMapAllFieldsCorrectly() {
            CurrentUserInfoDto dto = userMapper.toCurrentUserInfoDto(testUser);

            assertNotNull(dto);

            assertEquals(testUser.getId(), dto.getId(), "Id field should be mapped correctly");
            assertEquals(testUser.getEmail(), dto.getEmail(), "Email field should be mapped correctly");
            assertEquals(testUser.getRole(), dto.getRole(), "Role field should be mapped correctly");
        }

        @Test
        @DisplayName("Should return null when the input User is null")
        void toCurrentUserInfoDto_ShouldReturnNullWhenUserIsNull() {
            assertNull(userMapper.toCurrentUserInfoDto(null));
        }
    }
}