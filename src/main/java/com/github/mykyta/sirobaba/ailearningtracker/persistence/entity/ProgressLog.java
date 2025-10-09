package com.github.mykyta.sirobaba.ailearningtracker.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by Mykyta Sirobaba on 15.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Entity
@Table(name = "progress_logs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_time", nullable = false)
    private LocalDateTime logTime;

    @Column(name = "minutes_spent", nullable = false)
    private Integer minutesSpent;

    @Column(length = 1000)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;
}

