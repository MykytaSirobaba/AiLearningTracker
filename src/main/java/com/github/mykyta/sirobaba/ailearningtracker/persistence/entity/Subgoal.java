package com.github.mykyta.sirobaba.ailearningtracker.persistence.entity;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Mykyta Sirobaba on 15.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Entity
@Table(name = "subgoals")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subgoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(name = "is_completed", nullable = false)
    private boolean completed = false;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;
}
