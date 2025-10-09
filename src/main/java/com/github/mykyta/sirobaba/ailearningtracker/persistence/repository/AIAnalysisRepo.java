package com.github.mykyta.sirobaba.ailearningtracker.persistence.repository;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.AIAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Mykyta Sirobaba on 15.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Repository
public interface AIAnalysisRepo extends JpaRepository<AIAnalysis, Long> {
}
