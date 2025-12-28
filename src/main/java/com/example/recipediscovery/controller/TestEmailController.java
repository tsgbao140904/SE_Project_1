package com.example.recipediscovery.controller;

import com.example.recipediscovery.scheduler.MealPlanNotificationScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * ⚠️ TEST CONTROLLER - XÓA SAU KHI TEST XONG! ⚠️
 * Controller tạm để test gửi email không cần đợi scheduler
 */
@RestController
@RequestMapping("/test")
public class TestEmailController {

    private final MealPlanNotificationScheduler scheduler;

    public TestEmailController(MealPlanNotificationScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @GetMapping("/breakfast-email")
    public String testBreakfast() {
        scheduler.sendBreakfastNotifications();
        return "✅ Đã trigger breakfast email lúc: " + LocalDateTime.now() + 
               "<br><br>Kiểm tra:<br>1. Console log trong IntelliJ<br>2. Gmail inbox: thaibao9714@gmail.com";
    }

    @GetMapping("/lunch-email")
    public String testLunch() {
        scheduler.sendLunchNotifications();
        return "✅ Đã trigger lunch email lúc: " + LocalDateTime.now() + 
               "<br><br>Kiểm tra:<br>1. Console log trong IntelliJ<br>2. Gmail inbox: thaibao9714@gmail.com";
    }

    @GetMapping("/snack-email")
    public String testSnack() {
        scheduler.sendSnackNotifications();
        return "✅ Đã trigger snack email lúc: " + LocalDateTime.now() + 
               "<br><br>Kiểm tra:<br>1. Console log trong IntelliJ<br>2. Gmail inbox: thaibao9714@gmail.com";
    }

    @GetMapping("/dinner-email")
    public String testDinner() {
        scheduler.sendDinnerNotifications();
        return "✅ Đã trigger dinner email lúc: " + LocalDateTime.now() + 
               "<br><br>Kiểm tra:<br>1. Console log trong IntelliJ<br>2. Gmail inbox: thaibao9714@gmail.com";
    }

    @GetMapping("/")
    public String index() {
        return "<h1>🧪 Email Test Controller</h1>" +
               "<p>Click vào link để test gửi email:</p>" +
               "<ul>" +
               "<li><a href='/test/breakfast-email'>Test Breakfast Email</a></li>" +
               "<li><a href='/test/lunch-email'>Test Lunch Email</a></li>" +
               "<li><a href='/test/snack-email'>Test Snack Email</a></li>" +
               "<li><a href='/test/dinner-email'>Test Dinner Email</a></li>" +
               "</ul>" +
               "<p><strong>⚠️ Nhớ xóa file này sau khi test xong!</strong></p>";
    }
}
