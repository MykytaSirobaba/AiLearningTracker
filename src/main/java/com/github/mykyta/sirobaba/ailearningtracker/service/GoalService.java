package com.github.mykyta.sirobaba.ailearningtracker.service;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;

import java.util.List;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface GoalService {
    GoalResponseDto createGoal(GoalRequestDto goalRequestDto);
    void removeGoal(Integer id);
    List<GoalResponseDto> getAllGoals();
}
