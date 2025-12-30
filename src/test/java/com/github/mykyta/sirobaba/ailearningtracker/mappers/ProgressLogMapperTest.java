package com.github.mykyta.sirobaba.ailearningtracker.mappers;

import com.github.mykyta.sirobaba.ailearningtracker.ModelUtils;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogDetailsResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.ProgressLog;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.ProgressLogMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static com.github.mykyta.sirobaba.ailearningtracker.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Tag("Mapper")
@DisplayName("ProgressLogMapper Tests")
class ProgressLogMapperTest {

    private final ProgressLogMapper mapper = Mappers.getMapper(ProgressLogMapper.class);

    private final ProgressLog testProgressLog = ModelUtils.createTestProgressLog();

    @Nested
    @DisplayName("Method: progressLogToProgressLogResponseDto")
    class ProgressLogResponseDtoTests {

        @Test
        @DisplayName("Should map all fields and apply custom formatting for time")
        void progressLogToProgressLogResponseDto_ShouldMapAllFieldsCorrectly() {
            ProgressLogResponseDto dto = mapper.progressLogToProgressLogResponseDto(testProgressLog);

            assertNotNull(dto);
            assertEquals(TEST_ID, dto.getProgressLogId());
            assertEquals(TEST_MINUTES_SPENT_ON_TASK, dto.getTotalMinutes());
            assertEquals("2 hours 15 mins", dto.getFormattedTime());
            assertEquals(TEST_LOG_TIME, dto.getLogTime());
        }

        @Test
        @DisplayName("Should return null when input entity is null")
        void progressLogToProgressLogResponseDto_ShouldReturnNull() {
            assertNull(mapper.progressLogToProgressLogResponseDto(null));
        }
    }

    @Nested
    @DisplayName("Method: progressLogToProgressLogDetailsResponseDto")
    class ProgressLogDetailsResponseDtoTests {

        @Test
        @DisplayName("Should map all fields and apply custom date and time formatting")
        void progressLogToProgressLogDetailsResponseDto_ShouldMapAllFieldsCorrectly() {
            ProgressLogDetailsResponseDto dto = mapper.progressLogToProgressLogDetailsResponseDto(testProgressLog);

            assertNotNull(dto);
            assertEquals(TEST_ID, dto.getProgressLogId());
            assertEquals(TEST_MINUTES_SPENT_ON_TASK, dto.getTotalMinutes());
            assertEquals("2 hours 15 mins", dto.getFormattedTime());
            assertEquals(TEST_LOG_TIME.toLocalDate(), dto.getLogTime());
            assertEquals(TEST_PROGRESS_LOG_NOTE, dto.getNote());
        }

        @Test
        @DisplayName("Should return null when input entity is null")
        void progressLogToProgressLogDetailsResponseDto_ShouldReturnNull() {
            assertNull(mapper.progressLogToProgressLogDetailsResponseDto(null));
        }
    }

    @Nested
    @DisplayName("Named Qualifier Methods")
    class QualifierMethodTests {

        @Test
        @DisplayName("formatMinutes: Should format hours and minutes correctly (135 min -> 2 hours 15 mins)")
        void formatMinutes_ShouldFormatHoursAndMinutes() {
            assertEquals("2 hours 15 mins", mapper.formatMinutes(135));
        }

        @Test
        @DisplayName("formatMinutes: Should format only minutes correctly (50 min -> 50 mins)")
        void formatMinutes_ShouldFormatOnlyMinutes() {
            assertEquals("50 mins", mapper.formatMinutes(50));
        }

        @Test
        @DisplayName("formatMinutes: Should format single minute correctly (1 min -> 1 min)")
        void formatMinutes_ShouldFormatSingleMinute() {
            assertEquals("1 min", mapper.formatMinutes(1));
        }

        @Test
        @DisplayName("formatMinutes: Should handle null input")
        void formatMinutes_ShouldHandleNull() {
            assertEquals("0 min", mapper.formatMinutes(null));
        }

        @Test
        @DisplayName("toLocalDate: Should convert LocalDateTime to LocalDate")
        void toLocalDate_ShouldConvertLocalDateTime() {
            LocalDate expectedDate = LocalDate.of(2025, 11, 18);
            assertEquals(expectedDate, mapper.toLocalDate(TEST_LOG_TIME));
        }

        @Test
        @DisplayName("toLocalDate: Should handle null input")
        void toLocalDate_ShouldHandleNull() {
            assertNull(mapper.toLocalDate(null));
        }
    }
}