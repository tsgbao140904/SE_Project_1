package com.example.recipediscovery.controller.user;

import com.example.recipediscovery.dto.SessionUser;
import com.example.recipediscovery.model.Recipe;
import com.example.recipediscovery.model.UserCategory;
import com.example.recipediscovery.repository.RecipeRepository;
import com.example.recipediscovery.service.RecipeService;
import com.example.recipediscovery.service.UserCategoryService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller chính cho người dùng (User)
 * - Trang chủ cá nhân
 * - Tính toán dinh dưỡng
 * - API gợi ý công thức theo calo
 */
@Controller
public class UserController {

    // ============================== DEPENDENCIES ==============================
    private final RecipeService recipeService;
    private final UserCategoryService userCategoryService;
    private final RecipeRepository recipeRepository;

    // ============================== CONSTRUCTOR ==============================
    public UserController(RecipeService recipeService,
                          UserCategoryService userCategoryService,
                          RecipeRepository recipeRepository) {
        this.recipeService = recipeService;
        this.userCategoryService = userCategoryService;
        this.recipeRepository = recipeRepository;
    }

    // ============================== PAGE ROUTES ==============================

    /**
     * Trang chủ cá nhân - Danh sách công thức của người dùng
     */
    @GetMapping("/app/home")
    public String home(@RequestParam(required = false) String q,
                       @RequestParam(name = "userCategoryId", required = false) Long categoryId,
                       HttpSession session,
                       Model model) {

        SessionUser su = (SessionUser) session.getAttribute("USER");
        if (su == null) {
            return "redirect:/login";
        }

        model.addAttribute("categories", userCategoryService.getByUser(su.getId()));
        model.addAttribute("recipes", recipeService.searchUserRecipes(su.getId(), q, categoryId));
        model.addAttribute("keyword", q);
        model.addAttribute("userCategoryId", categoryId);

        return "user/home";
    }

    /**
     * Trang tính toán nhu cầu dinh dưỡng
     */
    @GetMapping("/app/nutrition-calculator")
    public String nutritionCalculator(HttpSession session) {
        SessionUser su = (SessionUser) session.getAttribute("USER");
        return (su == null) ? "redirect:/login" : "user/nutrition-calculator";
    }

    // ============================== API ENDPOINTS ==============================

    /**
     * API gợi ý công thức phù hợp
     * Chỉ trả về công thức có calo ≤ nhu cầu người dùng (tối đa bằng calo đã tính)
     * Ưu tiên: công thức cá nhân → nếu không có thì lấy từ cộng đồng đã duyệt
     * Sắp xếp theo gần nhất (cao nhất nhưng vẫn ≤ calo mục tiêu)
     */
    /**
     * GỢI Ý CÔNG THỨC + PHÂN TRANG
     * page: bắt đầu từ 0
     * size: mặc định 12
     */
    @GetMapping("/app/api/recipes/suggest")
    @ResponseBody
    public Map<String, Object> suggestRecipes(
            @RequestParam int calo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            HttpSession session) {

        SessionUser su = (SessionUser) session.getAttribute("USER");
        if (su == null) {
            return Map.of("recipes", List.of(), "hasMore", false);
        }

        Long userId = su.getId();
        int offset = page * size;

        // 1. Lấy tất cả công thức phù hợp (cá nhân + cộng đồng)
        List<Recipe> allMatches = new ArrayList<>();

        // Công thức cá nhân
        allMatches.addAll(recipeRepository.findByUserId(userId).stream()
                .filter(r -> r.getCalories() != null && r.getCalories() <= calo)
                .toList());

        // Công thức cộng đồng (loại trùng với cá nhân)
        allMatches.addAll(recipeRepository.findByShareStatusAndIsPublic("APPROVED", 1).stream()
                .filter(r -> r.getCalories() != null && r.getCalories() <= calo)
                .filter(r -> allMatches.stream().noneMatch(m -> m.getId().equals(r.getId())))
                .toList());

        // Sắp xếp: calo cao nhất trước (gần nhu cầu nhất)
        allMatches.sort((a, b) -> Integer.compare(b.getCalories(), a.getCalories()));

        // Phân trang
        int total = allMatches.size();
        int toIndex = Math.min(offset + size, total);
        List<Recipe> pageRecipes = allMatches.subList(offset, toIndex);

        // Chuyển sang Map để tránh lỗi Hibernate proxy
        List<Map<String, Object>> result = pageRecipes.stream()
                .map(this::toSimpleMap)
                .toList();

        return Map.of(
                "recipes", result,
                "hasMore", toIndex < total,
                "total", total,
                "currentPage", page,
                "totalPages", (int) Math.ceil((double) total / size)
        );
    }

    // Giữ nguyên helper (tránh lỗi lazy load)
    private Map<String, Object> toSimpleMap(Recipe r) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", r.getId());
        map.put("title", r.getTitle());
        map.put("imageUrl", r.getImageUrl());
        map.put("calories", r.getCalories());
        map.put("cookingTime", r.getCookingTime());

        if (r.getUserCategory() != null && Hibernate.isInitialized(r.getUserCategory())) {
            Map<String, Object> cat = new HashMap<>();
            cat.put("name", r.getUserCategory().getName());
            cat.put("colorCode", r.getUserCategory().getColorCode());
            cat.put("icon", r.getUserCategory().getIcon());
            map.put("userCategory", cat);
        } else {
            map.put("userCategory", null);
        }
        return map;
    }
}