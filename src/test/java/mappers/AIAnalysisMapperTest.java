package mappers;

import ailearningtracker.ModelUtils;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiAnalysisOfProgressLogDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisDetailsDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.AIAnalysis;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.AIAnalysisMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static ailearningtracker.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@DisplayName("AIAnalysisMapper Tests")
class AIAnalysisMapperTest {

    private final AIAnalysisMapper aiAnalysisMapper = Mappers.getMapper(AIAnalysisMapper.class);


    private final AIAnalysis testAIAnalysis = ModelUtils.createTestAIAnalysis();
    private AiAnalysisOfProgressLogDto testAiAnalysisDto;

    @BeforeEach
    void setUp() {
        testAiAnalysisDto = new AiAnalysisOfProgressLogDto();
        testAiAnalysisDto.setAnalysisText(TEST_ANALYSIS_TEXT);
        testAiAnalysisDto.setCreatedAt(TEST_CREATED_AT);
        testAiAnalysisDto.setTitle(TEST_AI_ANALYSIS_TITLE);

    }

    @Nested
    @DisplayName("Method: toAIAnalysis (DTO to Entity)")
    class ToAIAnalysisTests {

        @Test
        @DisplayName("Should map AnalysisDto to AIAnalysis entity correctly")
        void toAIAnalysis_ShouldMapAllFieldsCorrectly() {
            AIAnalysis entity = aiAnalysisMapper.toAIAnalysis(testAiAnalysisDto);

            assertNotNull(entity);
            assertEquals(TEST_ANALYSIS_TEXT, entity.getAnalysisText());
            assertEquals(TEST_CREATED_AT, entity.getCreatedAt());
            assertEquals(TEST_AI_ANALYSIS_TITLE, entity.getTitle());
            assertNull(entity.getId());
        }

        @Test
        @DisplayName("Should return null when input DTO is null")
        void toAIAnalysis_ShouldReturnNullWhenInputIsNull() {
            assertNull(aiAnalysisMapper.toAIAnalysis(null));
        }
    }

    @Nested
    @DisplayName("Method: toAIAnalysisDetailsDto (Entity to Details DTO)")
    class ToAIAnalysisDetailsDtoTests {

        @Test
        @DisplayName("Should map all fields including the full analysis text")
        void toAIAnalysisDetailsDto_ShouldMapFieldsCorrectly() {
            AIAnalysisDetailsDto dto = aiAnalysisMapper.toAIAnalysisDetailsDto(testAIAnalysis);

            assertNotNull(dto);
            assertEquals(TEST_ID, dto.getId());
            assertEquals(TEST_AI_ANALYSIS_TITLE, dto.getTitle());
            assertEquals(TEST_ANALYSIS_TEXT, dto.getAnalysisText());
            assertEquals(TEST_CREATED_AT, dto.getCreatedAt());
        }

        @Test
        @DisplayName("Should return null when input Entity is null")
        void toAIAnalysisDetailsDto_ShouldReturnNullWhenInputIsNull() {
            assertNull(aiAnalysisMapper.toAIAnalysisDetailsDto(null));
        }
    }
}