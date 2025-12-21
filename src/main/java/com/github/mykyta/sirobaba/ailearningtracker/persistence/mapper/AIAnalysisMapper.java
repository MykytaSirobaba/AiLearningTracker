package com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiAnalysisOfProgressLogDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisDetailsDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.AIAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper responsible for converting AI-generated progress analysis DTOs into
 * {@link AIAnalysis} database entities and mapping them back into various response DTOs.
 */
@Mapper(componentModel = "spring")
public interface AIAnalysisMapper {

    /**
     * Converts AI analysis result received from AI service into a persistent entity.
     *
     * @param aiAnalysisDto AI response containing analysis text and timestamp
     * @return mapped AIAnalysis entity
     */
    @Mapping(source = "analysisText", target = "analysisText")
    @Mapping(source = "createdAt", target = "createdAt")
    AIAnalysis toAIAnalysis(AiAnalysisOfProgressLogDto aiAnalysisDto);

    /**
     * Converts a saved AIAnalysis entity into a detailed DTO for API responses.
     *
     * @param aiAnalysis entity from DB
     * @return detailed analysis DTO
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "analysisText", target = "analysisText")
    @Mapping(source = "createdAt", target = "createdAt")
    AIAnalysisDetailsDto toAIAnalysisDetailsDto(AIAnalysis aiAnalysis);
}

