package com.github.mykyta.sirobaba.ailearningtracker.persistence.entity;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mykyta Sirobaba on 15.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Entity
@Table(name = "goals")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "solved_at")
    private LocalDate solvedAt;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "is_completed", nullable = false)
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "goal", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subgoal> subgoals = new ArrayList<>();

    @OneToMany(mappedBy = "goal", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgressLog> progressLogs = new ArrayList<>();

    @OneToMany(mappedBy = "goal", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AIAnalysis> aiAnalyses = new ArrayList<>();
}
