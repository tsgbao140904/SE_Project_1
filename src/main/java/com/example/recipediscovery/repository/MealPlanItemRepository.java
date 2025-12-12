// MealPlanItemRepository.java
package com.example.recipediscovery.repository;

import com.example.recipediscovery.model.MealPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealPlanItemRepository extends JpaRepository<MealPlanItem, Long> {
    List<MealPlanItem> findByMealPlanId(Long mealPlanId);
}