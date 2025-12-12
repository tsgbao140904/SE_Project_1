// MealPlanItem.java
package com.example.recipediscovery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_plan_items")
public class MealPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;  // 1 = Thứ 2, ..., 7 = Chủ nhật

    @Column(name = "meal_type", nullable = false)
    private String mealType;    // BREAKFAST, LUNCH, SNACK, DINNER

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;      // có thể null

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MealPlan getMealPlan() { return mealPlan; }
    public void setMealPlan(MealPlan mealPlan) { this.mealPlan = mealPlan; }

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}