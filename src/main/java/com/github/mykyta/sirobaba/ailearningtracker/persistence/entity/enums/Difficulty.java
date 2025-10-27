package com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums;

import lombok.Getter;

/**
 * Created by Mykyta Sirobaba on 15.08.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Getter
public enum Difficulty {
    EASY(10),
    MEDIUM(20),
    HARD(30);

    private final int estimatedHours;

    Difficulty(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
}
