package com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogDetailsResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.ProgressLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mapper for converting {@link ProgressLog} entities into summary and detailed DTOs.
 * <p>
 * Contains helper formatting methods for minutes → readable string ("1 hour 20 mins")
 * and LocalDateTime → LocalDate mapping.
 */
@Mapper(componentModel = "spring")
public interface ProgressLogMapper {

    /**
     * Maps ProgressLog to ProgressLogResponseDto with formatted time fields.
     *
     * @param entity the progress log entity
     * @return formatted summary DTO
     */
    @Mapping(target = "progressLogId", source = "id")
    @Mapping(target = "totalMinutes", source = "minutesSpent")
    @Mapping(target = "formattedTime", source = "minutesSpent", qualifiedByName = "formatMinutes")
    ProgressLogResponseDto progressLogToProgressLogResponseDto(ProgressLog entity);

    /**
     * Maps ProgressLog into a detailed DTO including LocalDate and note.
     *
     * @param entity the entity to map
     * @return detailed DTO
     */
    @Mapping(target = "progressLogId", source = "id")
    @Mapping(target = "totalMinutes", source = "minutesSpent")
    @Mapping(target = "formattedTime", source = "minutesSpent", qualifiedByName = "formatMinutes")
    @Mapping(target = "logTime", source = "logTime", qualifiedByName = "toLocalDate")
    @Mapping(target = "note", source = "note")
    ProgressLogDetailsResponseDto progressLogToProgressLogDetailsResponseDto(ProgressLog entity);

    /**
     * Formats minutes into a human-readable time string.
     *
     * @param minutes number of minutes
     * @return formatted string ("90 mins", "1 hour 30 mins")
     */
    @Named("formatMinutes")
    default String formatMinutes(Integer minutes) {
        if (minutes == null) {
            return "0 min";
        }
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (hours > 0) {
            return String.format("%d hour%s %d min%s", hours, hours > 1 ? "s" : "", remainingMinutes, remainingMinutes != 1 ? "s" : "");
        } else {
            return String.format("%d min%s", remainingMinutes, remainingMinutes != 1 ? "s" : "");
        }
    }

    /**
     * Converts LocalDateTime → LocalDate.
     *
     * @param dateTime original datetime
     * @return date part or null
     */
    @Named("toLocalDate")
    default LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }
}
