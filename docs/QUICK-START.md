# 🚀 HƯỚNG DẪN CHẠY VÀ TEST - NHANH

## ✅ ĐÃ HOÀN THÀNH

- ✅ Cập nhật `application.yml` với email: **thaibao9714@gmail.com**
- ✅ Cập nhật App Password: **dhiopbbqyfdmoazw**
- ✅ Tạo **TestEmailController.java** để test ngay

---

## 🏃 CÁCH CHẠY ỨNG DỤNG

### Option 1: Chạy từ IntelliJ (KHUYẾN NGHỊ)

#### Bước 1: Mở RecipeDiscoveryApplication.java
- File: `src/main/java/com/example/recipediscovery/RecipeDiscoveryApplication.java`

#### Bước 2: Click Run
1. Click chuột phải vào file `RecipeDiscoveryApplication.java`
2. Chọn **"Run 'RecipeDiscoveryApplication'"**

HOẶC:

1. Click vào biểu tượng ▶️ (màu xanh) bên trái dòng `public static void main`
2. Chọn **"Run 'RecipeDiscoveryApplication.main()'"**

#### Bước 3: Đợi ứng dụng khởi động
```
...
2025-12-29 02:25:00.123  INFO --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http)
2025-12-29 02:25:00.456  INFO --- [main] c.e.r.RecipeDiscoveryApplication        : Started RecipeDiscoveryApplication in 5.123 seconds
```

✅ **Thấy dòng "Started RecipeDiscoveryApplication" = Thành công!**

---

## 📧 TEST EMAIL NGAY LẬP TỨC

### Cách 1: Test bằng Browser (NHANH NHẤT)

#### Bước 1: Mở browser
Sau khi app đã chạy, mở browser và vào:

```
http://localhost:8080/test/
```

Bạn sẽ thấy trang với 4 link test.

#### Bước 2: Click vào link test
Click vào: **"Test Breakfast Email"**

URL: http://localhost:8080/test/breakfast-email

#### Bước 3: Xem kết quả
- Browser hiện: "✅ Đã trigger breakfast email lúc: ..."
- **Console trong IntelliJ** sẽ hiện log:
  ```
  🍳 Starting BREAKFAST notifications at: ...
  ✉️ Sent BREAKFAST notification to user@email.com
  ✅ Completed BREAKFAST notifications. Sent X emails.
  ```

#### Bước 4: Kiểm tra Gmail
- Mở https://gmail.com
- Login: thaibao9714@gmail.com
- Tìm email mới: **"🍳 Thời gian ăn sáng - Recipe Discovery"**

---

### Cách 2: Test với Scheduler (ĐỢI 2 PHÚT)

Nếu muốn test scheduler tự động:

#### Bước 1: Sửa cron expression
Mở `MealPlanNotificationScheduler.java`

Tìm dòng 45:
```java
@Scheduled(cron = "0 0 7 * * ?")
public void sendBreakfastNotifications() {
```

**SỬA THÀNH:**
```java
@Scheduled(cron = "0 */2 * * * ?")  // Chạy mỗi 2 phút để test
public void sendBreakfastNotifications() {
    logger.info("🍳 [TEST] Running breakfast at: {}", LocalDateTime.now());
```

#### Bước 2: Restart app
- Stop app (click nút Stop ⬛ màu đỏ)
- Run lại app

#### Bước 3: Đợi 2 phút
Console sẽ tự động chạy và hiện log sau 2 phút.

#### Bước 4: Kiểm tra email
Vào Gmail inbox xem có email mới không.

**⚠️ NHỚ SỬA LẠI** cron về `0 0 7 * * ?` sau khi test xong!

---

## 🐛 XỬ LÝ LỖI

### Lỗi 1: "Authentication failed"
**Nguyên nhân:** App Password sai hoặc 2FA chưa bật

**Giải pháp:**
1. Vào https://myaccount.google.com/security
2. Kiểm tra 2-Step Verification đã bật chưa
3. Tạo lại App Password mới
4. Cập nhật lại vào `application.yml`

### Lỗi 2: "Could not connect to SMTP host"
**Nguyên nhân:** Firewall hoặc không có internet

**Giải pháp:**
1. Kiểm tra internet
2. Tắt firewall/antivirus tạm
3. Thử đổi port 587 → 465 trong `application.yml`

### Lỗi 3: "No users found" hoặc "Sent 0 emails"
**Nguyên nhân:** 
- Database không có users
- Users không có email
- Users không có meal plan cho ngày hôm nay

**Giải pháp:**
1. Kiểm tra database có users không
2. Verify users có email field
3. Tạo meal plan cho user trong app
4. Thêm món vào meal plan

---

## 📊 KIỂM TRA KẾT QUẢ

### Checklist Email Content:
- [ ] Subject: "🍳 Thời gian ăn sáng - Recipe Discovery"
- [ ] Tên user hiển thị trong email
- [ ] Icon bữa ăn (🍳) và khung giờ (07:00-09:00)
- [ ] Hình ảnh món ăn
- [ ] Calories, cooking time, servings
- [ ] Nguyên liệu (có xuống dòng)
- [ ] Hướng dẫn nấu
- [ ] Button "Xem kế hoạch bữa ăn"
- [ ] Email đẹp trên mobile

---

## 🧹 SAU KHI TEST XONG

### Bước 1: Xóa TestEmailController
**XÓA FILE:** `src/main/java/com/example/recipediscovery/controller/TestEmailController.java`

### Bước 2: Sửa lại cron (nếu đã sửa)
Trong `MealPlanNotificationScheduler.java`, đổi lại:
```java
@Scheduled(cron = "0 0 7 * * ?")  // 7h sáng
@Scheduled(cron = "0 0 11 * * ?") // 11h trưa  
@Scheduled(cron = "0 0 15 * * ?") // 3h chiều
@Scheduled(cron = "0 0 17 * * ?") // 5h chiều
```

### Bước 3: Restart app
- Stop app
- Run lại
- Giờ scheduler sẽ chạy đúng giờ production!

---

## 🎉 DONE!

Hệ thống email notification đã sẵn sàng!

**Scheduler sẽ tự động gửi email:**
- 🍳 07:00 - Breakfast
- 🍽️ 11:00 - Lunch
- ☕ 15:00 - Snack
- 🌙 17:00 - Dinner

**Không cần làm gì thêm** - chỉ cần đảm bảo app luôn chạy!
