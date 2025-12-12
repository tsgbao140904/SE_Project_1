// MealPlanService.java
package com.example.recipediscovery.service;

import com.example.recipediscovery.model.*;
import com.example.recipediscovery.repository.MealPlanItemRepository;
import com.example.recipediscovery.repository.MealPlanRepository;
import com.example.recipediscovery.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepo;
    private final MealPlanItemRepository itemRepo;
    private final RecipeRepository recipeRepo;

    public MealPlanService(MealPlanRepository mealPlanRepo,
                           MealPlanItemRepository itemRepo,
                           RecipeRepository recipeRepo) {
        this.mealPlanRepo = mealPlanRepo;
        this.itemRepo = itemRepo;
        this.recipeRepo = recipeRepo;
    }

    /**
     * Lấy hoặc tạo mới MealPlan cho tuần hiện tại (tuần bắt đầu từ Thứ 2)
     */
    public MealPlan getOrCreateCurrentWeekPlan(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY); // Thứ 2

        Optional<MealPlan> existing = mealPlanRepo.findByUserIdAndWeekStartDate(userId, weekStart);
        return existing.orElseGet(() -> {
            MealPlan plan = new MealPlan();
            plan.setUserId(userId);
            plan.setWeekStartDate(weekStart);
            return mealPlanRepo.save(plan);
        });
    }

    /**
     * Lấy toàn bộ items của 1 plan
     */
    public List<MealPlanItem> getItemsByPlan(Long planId) {
        return itemRepo.findByMealPlanId(planId);
    }

    /**
     * Thêm hoặc cập nhật món vào lịch
     */
    @Transactional
    public void updateMeal(Long planId, Integer dayOfWeek, String mealType, Long recipeId) {
        MealPlan plan = mealPlanRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy plan"));

        Recipe recipe = recipeId != null ? recipeRepo.findById(recipeId).orElse(null) : null;

        // Tìm item hiện có
        Optional<MealPlanItem> existing = itemRepo.findByMealPlanId(planId).stream()
                .filter(item -> item.getDayOfWeek().equals(dayOfWeek) && item.getMealType().equals(mealType))
                .findFirst();

        if (existing.isPresent()) {
            MealPlanItem item = existing.get();
            if (recipe == null) {
                itemRepo.delete(item); // xóa nếu bỏ trống
            } else {
                item.setRecipe(recipe);
                itemRepo.save(item);
            }
        } else {
            if (recipe != null) {
                MealPlanItem item = new MealPlanItem();
                item.setMealPlan(plan);
                item.setDayOfWeek(dayOfWeek);
                item.setMealType(mealType);
                item.setRecipe(recipe);
                itemRepo.save(item);
            }
        }
    }

    /**
     * Xóa món
     */
    @Transactional
    public void removeMeal(Long planId, Integer dayOfWeek, String mealType) {
        updateMeal(planId, dayOfWeek, mealType, null);
    }
}