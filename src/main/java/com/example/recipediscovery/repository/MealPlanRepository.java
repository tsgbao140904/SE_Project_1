// MealPlanRepository.java
package com.example.recipediscovery.repository;

import com.example.recipediscovery.model.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    Optional<MealPlan> findByUserIdAndWeekStartDate(Long userId, LocalDate weekStartDate);
}