package com.example.recipediscovery.service;

import com.example.recipediscovery.model.MealPlanItem;
import com.example.recipediscovery.model.Recipe;
import com.example.recipediscovery.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.username:noreply@recipediscovery.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Gửi email thông báo meal plan cho user
     */
    public void sendMealPlanNotification(User user, MealPlanItem item, String mealType) {
        try {
            Recipe recipe = item.getRecipe();
            if (recipe == null) {
                logger.warn("MealPlanItem {} has no recipe, skipping email", item.getId());
                return;
            }

            // Tạo email context
            Context context = new Context();
            context.setVariable("userName", user.getFullName());
            context.setVariable("mealType", mealType);
            context.setVariable("mealIcon", getMealIcon(mealType));
            context.setVariable("mealTime", getMealTime(mealType));
            context.setVariable("recipe", recipe);
            context.setVariable("baseUrl", baseUrl);

            // Render HTML content
            String htmlContent = templateEngine.process("emails/meal-notification-email", context);

            // Tạo và gửi email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject(getEmailSubject(mealType));
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✉️ Sent {} notification to {}", mealType, user.getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", user.getEmail(), e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error sending email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    private String getEmailSubject(String mealType) {
        return switch (mealType) {
            case "BREAKFAST" -> "🍳 Thời gian ăn sáng - Recipe Discovery";
            case "LUNCH" -> "🍽️ Thời gian ăn trưa - Recipe Discovery";
            case "SNACK" -> "☕ Thời gian ăn nhẹ - Recipe Discovery";
            case "DINNER" -> "🌙 Thời gian ăn tối - Recipe Discovery";
            default -> "🔔 Nhắc nhở bữa ăn - Recipe Discovery";
        };
    }

    private String getMealIcon(String mealType) {
        return switch (mealType) {
            case "BREAKFAST" -> "🍳";
            case "LUNCH" -> "🍽️";
            case "SNACK" -> "☕";
            case "DINNER" -> "🌙";
            default -> "🍴";
        };
    }

    private String getMealTime(String mealType) {
        return switch (mealType) {
            case "BREAKFAST" -> "07:00 - 09:00";
            case "LUNCH" -> "11:00 - 13:00";
            case "SNACK" -> "15:00 - 17:00";
            case "DINNER" -> "17:00 - 21:00";
            default -> "";
        };
    }
}
