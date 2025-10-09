package com.github.mykyta.sirobaba.ailearningtracker.service.impl;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.service.GoalService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Service
public class GoalServiceImpl implements GoalService {
    @Override
    public GoalResponseDto createGoal(GoalRequestDto goalRequestDto) {
        return null;
    }

    @Override
    public void removeGoal(Integer id) {

    }

    @Override
    public List<GoalResponseDto> getAllGoals() {
        return List.of();
    }
}
