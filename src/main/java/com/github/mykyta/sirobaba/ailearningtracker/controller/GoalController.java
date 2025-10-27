package com.github.mykyta.sirobaba.ailearningtracker.controller;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.service.GoalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
    public ResponseEntity<GoalResponseDto> createGoal(@RequestBody GoalRequestDto goalRequest, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.createGoal(goalRequest, principal));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<GoalResponseDto> deleteGoal(@PathVariable Long goalId){
        goalService.removeGoal(goalId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/goals")
    public ResponseEntity<List<GoalResponseDto>> getAllGoals(){
        return ResponseEntity.status(HttpStatus.OK).body(goalService.getAllGoals());
    }
}
