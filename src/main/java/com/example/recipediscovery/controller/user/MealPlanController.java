// MealPlanController.java
package com.example.recipediscovery.controller.user;

import com.example.recipediscovery.dto.SessionUser;
import com.example.recipediscovery.model.*;
import com.example.recipediscovery.service.MealPlanService;
import com.example.recipediscovery.service.RecipeService;
import com.example.recipediscovery.service.UserCategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/app/meal-plan")
public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final RecipeService recipeService;
    private final UserCategoryService userCategoryService;

    public MealPlanController(MealPlanService mealPlanService,
                              RecipeService recipeService,
                              UserCategoryService userCategoryService) {
        this.mealPlanService = mealPlanService;
        this.recipeService = recipeService;
        this.userCategoryService = userCategoryService;
    }

    @GetMapping
    public String showMealPlan(HttpSession session, Model model) {
        SessionUser su = (SessionUser) session.getAttribute("USER");
        if (su == null) return "redirect:/login";

        Long userId = su.getId();

        MealPlan plan = mealPlanService.getOrCreateCurrentWeekPlan(userId);
        List<MealPlanItem> items = mealPlanService.getItemsByPlan(plan.getId());

        // Nhóm theo ngày và bữa
        Map<Integer, Map<String, Recipe>> planMap = new HashMap<>();
        for (int day = 1; day <= 7; day++) {
            Map<String, Recipe> dayMeals = new HashMap<>();
            dayMeals.put("BREAKFAST", null);
            dayMeals.put("LUNCH", null);
            dayMeals.put("SNACK", null);
            dayMeals.put("DINNER", null);
            planMap.put(day, dayMeals);
        }

        for (MealPlanItem item : items) {
            planMap.get(item.getDayOfWeek()).put(item.getMealType(), item.getRecipe());
        }

        // Lấy danh sách recipe của user để chọn
        List<Recipe> userRecipes = recipeService.getRecipesByUser(userId);

        model.addAttribute("plan", plan);
        model.addAttribute("planMap", planMap);
        model.addAttribute("recipes", userRecipes);
        model.addAttribute("days", Arrays.asList("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"));
        model.addAttribute("mealTypes", Arrays.asList(
                Map.of("key", "BREAKFAST", "name", "Bữa sáng"),
                Map.of("key", "LUNCH", "name", "Bữa trưa"),
                Map.of("key", "SNACK", "name", "Bữa chiều"),
                Map.of("key", "DINNER", "name", "Bữa tối")
        ));

        return "user/meal-plan";
    }

    @PostMapping("/update")
    public String updateMeal(
            @RequestParam Long planId,
            @RequestParam Integer dayOfWeek,
            @RequestParam String mealType,
            @RequestParam(required = false) Long recipeId,
            HttpSession session) {

        SessionUser su = (SessionUser) session.getAttribute("USER");
        if (su == null) return "redirect:/login";

        mealPlanService.updateMeal(planId, dayOfWeek, mealType, recipeId);

        return "redirect:/app/meal-plan";
    }

    @PostMapping("/remove")
    public String removeMeal(
            @RequestParam Long planId,
            @RequestParam Integer dayOfWeek,
            @RequestParam String mealType,
            HttpSession session) {

        SessionUser su = (SessionUser) session.getAttribute("USER");
        if (su == null) return "redirect:/login";

        mealPlanService.removeMeal(planId, dayOfWeek, mealType);

        return "redirect:/app/meal-plan";
    }
}