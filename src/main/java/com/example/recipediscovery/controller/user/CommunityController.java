package com.example.recipediscovery.controller.user;

import com.example.recipediscovery.dto.SessionUser;
import com.example.recipediscovery.model.Recipe;
import com.example.recipediscovery.repository.RecipeRepository;
import com.example.recipediscovery.service.RecipeService; // ← thêm dòng này
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CommunityController {

    private final RecipeRepository recipeRepo;
    private final RecipeService recipeService; // ← thêm field này

    public CommunityController(RecipeRepository recipeRepo,
                               RecipeService recipeService) { // ← thêm vào constructor
        this.recipeRepo = recipeRepo;
        this.recipeService = recipeService;
    }

    // Trang cộng đồng – BỔ SUNG TÌM KIẾM (không xóa code cũ)
    @GetMapping("/app/community")
    public String community(@RequestParam(required = false) String q,  // ← thêm tham số q
                            @RequestParam(defaultValue = "0") int page, // giữ nguyên phân trang
                            Model model) {

        List<Recipe> recipes;
        if (q != null && !q.trim().isEmpty()) {
            // Khi có từ khóa → tìm kiếm
            recipes = recipeService.searchCommunityRecipes(q.trim());
        } else {
            // Không có từ khóa → lấy tất cả như cũ
            recipes = recipeRepo.findByShareStatusAndIsPublic("APPROVED", 1);
        }

        // Giữ nguyên phần phân trang của bạn (12 món/trang)
        int pageSize = 12;
        int totalRecipes = recipes.size();
        int totalPages = (int) Math.ceil((double) totalRecipes / pageSize);
        int start = page * pageSize;
        int end = Math.min(start + pageSize, totalRecipes);

        List<Recipe> currentRecipes = (totalRecipes == 0) ? recipes : recipes.subList(start, end);

        model.addAttribute("recipes", currentRecipes);
        model.addAttribute("keyword", q);           // để giữ từ khóa trong ô tìm kiếm
        model.addAttribute("pageNum", page);
        model.addAttribute("totalPages", totalPages);

        return "user/community-home";
    }

    // Chi tiết công thức cộng đồng – giữ nguyên 100%
    @GetMapping("/app/community/{id}")
    public String communityDetail(@PathVariable Long id,
                                  HttpSession session,
                                  Model model) {

        SessionUser su = (SessionUser) session.getAttribute("USER");
        if (su == null) return "redirect:/login";

        Recipe r = recipeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy"));

        if (!"APPROVED".equals(r.getShareStatus())) {
            return "redirect:/app/community";
        }

        boolean isOwner = r.getUserId().equals(su.getId());

        model.addAttribute("recipe", r);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isCommunity", true);

        return "user/recipe-detail";
    }
}