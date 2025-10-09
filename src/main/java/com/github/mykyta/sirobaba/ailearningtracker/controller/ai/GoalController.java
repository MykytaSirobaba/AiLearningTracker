package com.github.mykyta.sirobaba.ailearningtracker.controller.ai;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.service.GoalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@RestController
@RequestMapping("/goal")
@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/create")
    public ResponseEntity<GoalResponseDto> createGoal(GoalRequestDto goalRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.createGoal(goalRequest));
    }
}
