# 📧 Email Notification System - Tài Liệu

## Tổng Quan

Hệ thống gửi email thông báo tự động cho meal plan của users. Email được gửi theo 4 khung giờ mỗi ngày.

## Nội Dung Tài Liệu

### 1. [QUICK-START.md](./QUICK-START.md) ⚡
**Hướng dẫn nhanh để chạy và test ngay**
- Cách chạy app từ IntelliJ
- Test email trong 2 phút
- Xử lý lỗi cơ bản

👉 **ĐỌC FILE NÀY TRƯỚC!**

### 2. [SETUP-GUIDE.md](./SETUP-GUIDE.md) 📚
**Hướng dẫn chi tiết setup từ đầu**
- Lấy Gmail App Password
- Cập nhật application.yml
- Build với Maven
- Test đầy đủ
- Troubleshooting

### 3. [IMPLEMENTATION-PLAN.md](./IMPLEMENTATION-PLAN.md) 📋
**Kế hoạch kỹ thuật implementation**  
- Thiết kế hệ thống
- Các components
- Verification plan
- Technical notes

---

## Khung Giờ Gửi Email

| Bữa Ăn | Thời Gian | Icon |
|---------|-----------|------|
| BREAKFAST | 07:00 AM | 🍳 |
| LUNCH | 11:00 AM | 🍽️ |
| SNACK | 03:00 PM | ☕ |
| DINNER | 05:00 PM | 🌙 |

---

## Files Đã Tạo

### Backend
- `EmailService.java` - Service gửi email
- `MealPlanNotificationScheduler.java` - Scheduler với 4 cron jobs
- `TestEmailController.java` - Controller test tạm (xóa sau khi test)

### Templates
- `emails/meal-notification-email.html` - Email template HTML đẹp

### Config
- `application.yml` - Email credentials đã cập nhật
- `pom.xml` - Thêm spring-boot-starter-mail dependency
- `RecipeDiscoveryApplication.java` - Thêm @EnableScheduling

---

## Test Nhanh

1. Chạy app từ IntelliJ
2. Mở browser: http://localhost:8080/test/
3. Click "Test Breakfast Email"
4. Kiểm tra console log và Gmail inbox

---

**Ngày tạo:** 2025-12-29  
**Email:** thaibao9714@gmail.com
