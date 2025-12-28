# 🚀 Hướng Dẫn Setup & Test Email Notification

## BƯỚC 1️⃣: Lấy Gmail App Password

### Tại sao cần App Password?
Gmail KHÔNG cho phép dùng mật khẩu thông thường cho ứng dụng bên ngoài. Bạn PHẢI tạo "App Password" (mật khẩu ứng dụng).

### Các bước lấy App Password:

#### 1. Vào Google Account Security
🔗 Mở link này: https://myaccount.google.com/security

#### 2. Bật 2-Step Verification (nếu chưa có)
- Tìm phần **"2-Step Verification"** 
- Click vào và làm theo hướng dẫn để bật
- ⚠️ **Bắt buộc phải bật 2FA trước khi tạo App Password**

#### 3. Tạo App Password
- Sau khi bật 2-Step Verification, quay lại trang Security
- Tìm mục **"App passwords"** (Mật khẩu ứng dụng)
- Click vào **"App passwords"**

#### 4. Tạo mật khẩu mới
- **Select app**: Chọn **"Mail"**
- **Select device**: Chọn **"Other (Custom name)"**
- Nhập tên: `Recipe Discovery`
- Click **"Generate"**

#### 5. Lưu mật khẩu
- Google sẽ hiển thị mật khẩu **16 ký tự** (ví dụ: `abcd efgh ijkl mnop`)
- ✅ **COPY mật khẩu này** (bỏ khoảng trắng khi paste vào config)
- ⚠️ **Chỉ hiện 1 lần duy nhất** - không thể xem lại!

---

## BƯỚC 2️⃣: Cập Nhật application.yml

### File cần sửa: `src/main/resources/application.yml`

Tìm phần **spring.mail** (khoảng dòng 19-30) và **SỬA 2 DÒNG**:

**TRƯỚC KHI SỬA:**
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: YOUR_GMAIL@gmail.com      # ← Dòng này
    password: YOUR_GMAIL_APP_PASSWORD    # ← Dòng này
```

**SAU KHI SỬA:** (Thay bằng thông tin CỦA BẠN)
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-actual-email@gmail.com        # ← Email Gmail của bạn
    password: abcdefghijklmnop                   # ← App Password (16 ký tự, KHÔNG có khoảng trắng)
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### ⚠️ Lưu ý quan trọng:
- `username`: Email Gmail **THẬT** của bạn (ví dụ: `nguyenvana@gmail.com`)
- `password`: App Password 16 ký tự **KHÔNG CÓ KHOẢNG TRẮNG** (ví dụ: `abcdefghijklmnop`)
- **KHÔNG dùng** mật khẩu Gmail bình thường - sẽ BỊ LỖI!

### Ví dụ cụ thể:
```yaml
spring:
  mail:
    username: baonguyen2004@gmail.com
    password: xpqrmzthwkjvlnop
```

---

## BƯỚC 3️⃣: Build Ứng Dụng

### Mở Terminal trong IntelliJ hoặc CMD

**Cách 1: Trong IntelliJ IDEA**
- Click vào menu **View** → **Tool Windows** → **Terminal**
- Hoặc nhấn phím tắt: `Alt + F12`

**Cách 2: Dùng CMD/PowerShell**
- Mở Command Prompt hoặc PowerShell
- Navigate đến thư mục project:
```bash
cd "C:\Users\ASUS TUF GAMING\IdeaProjects\Project-SpringBoot-Sprint_2"
```

### Chạy lệnh build:

```bash
mvnw.cmd clean install
```

**Hoặc nếu có Maven global:**
```bash
mvn clean install
```

### Kết quả mong đợi:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 15.234 s
```

⚠️ **Nếu gặp lỗi:**
- Kiểm tra Java version: `java -version` (phải là Java 17)
- Kiểm tra kết nối internet (cần download dependencies)
- Đọc error message để biết lỗi gì

---

## BƯỚC 4️⃣: Test Email Notification

### Option 1: Test Nhanh (Khuyến nghị cho lần đầu)

#### 4.1. Sửa Scheduler để chạy ngay

Mở file: [`MealPlanNotificationScheduler.java`](file:///c:/Users/ASUS%20TUF%20GAMING/IdeaProjects/Project-SpringBoot-Sprint_2/src/main/java/com/example/recipediscovery/scheduler/MealPlanNotificationScheduler.java)

Tìm dòng (khoảng line 45):
```java
@Scheduled(cron = "0 0 7 * * ?")
public void sendBreakfastNotifications() {
```

**SỬA THÀNH** (chạy mỗi 2 phút để test):
```java
@Scheduled(cron = "0 */2 * * * ?")  // Chạy mỗi 2 phút
public void sendBreakfastNotifications() {
    logger.info("🍳 [TEST MODE] Running breakfast at: {}", LocalDateTime.now());
```

#### 4.2. Chạy ứng dụng

```bash
mvnw.cmd spring-boot:run
```

**Hoặc:**
```bash
mvn spring-boot:run
```

#### 4.3. Xem log

Sau khi app chạy, đợi 2 phút và xem console log:

```
2025-12-29 02:15:00 INFO  - 🍳 [TEST MODE] Running breakfast at: 2025-12-29T02:15:00
2025-12-29 02:15:00 DEBUG - Found 5 users with email out of 10 total users
2025-12-29 02:15:01 INFO  - ✉️ Sent BREAKFAST notification to user@gmail.com
2025-12-29 02:15:02 INFO  - ✅ Completed BREAKFAST notifications. Sent 3 emails.
```

#### 4.4. Kiểm tra email

- Mở Gmail inbox
- Tìm email với subject: **"🍳 Thời gian ăn sáng - Recipe Discovery"**
- Kiểm tra nội dung email có đẹp không

#### 4.5. Dừng app và SỬA LẠI

**QUAN TRỌNG:** Sau khi test xong, **NHỚ SỬA LẠI** cron về giờ thật:

```java
@Scheduled(cron = "0 0 7 * * ?")  // Sửa lại về 7h sáng
public void sendBreakfastNotifications() {
    logger.info("🍳 Starting BREAKFAST notifications at: {}", LocalDateTime.now());
```

Dừng app: Nhấn `Ctrl + C` trong terminal

---

### Option 2: Test với Controller (Không cần đợi)

#### 4.1. Tạo Test Controller tạm thời

Tạo file mới: `src/main/java/com/example/recipediscovery/controller/TestEmailController.java`

```java
package com.example.recipediscovery.controller;

import com.example.recipediscovery.scheduler.MealPlanNotificationScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return "✅ Đã gửi email! Kiểm tra console log và Gmail inbox.";
    }

    @GetMapping("/lunch-email")
    public String testLunch() {
        scheduler.sendLunchNotifications();
        return "✅ Đã gửi email bữa trưa! Kiểm tra console log và Gmail inbox.";
    }
}
```

#### 4.2. Chạy app

```bash
mvnw.cmd spring-boot:run
```

#### 4.3. Test bằng browser

Mở browser và truy cập:
- **Test breakfast:** http://localhost:8080/test/breakfast-email
- **Test lunch:** http://localhost:8080/test/lunch-email

#### 4.4. Xem kết quả

- Console sẽ hiện log gửi email
- Email sẽ được gửi ngay lập tức
- Kiểm tra Gmail inbox

#### 4.5. Xóa Test Controller

**SAU KHI TEST XONG**, XÓA file `TestEmailController.java` - không cần nữa!

---

## BƯỚC 5️⃣: Verify Email Content

Khi nhận được email, kiểm tra:

### Checklist Email:
- [ ] Subject đúng: "🍳 Thời gian ăn sáng - Recipe Discovery"
- [ ] Hiển thị đúng tên user
- [ ] Icon bữa ăn và khung giờ đúng
- [ ] Hình ảnh món ăn load được
- [ ] Thông tin món (calories, cooking time, servings) đầy đủ
- [ ] Nguyên liệu hiển thị với xuống dòng đúng
- [ ] Hướng dẫn nấu rõ ràng
- [ ] Button "Xem kế hoạch" có link đúng
- [ ] Email đẹp trên cả mobile và desktop

---

## 🐛 Xử Lý Lỗi Thường Gặp

### Lỗi 1: "Authentication failed"
**Nguyên nhân:** Sai username hoặc password

**Giải pháp:**
- Kiểm tra lại email trong `application.yml`
- Kiểm tra lại App Password (16 ký tự, không có khoảng trắng)
- Thử tạo lại App Password mới

### Lỗi 2: "Could not connect to SMTP host"
**Nguyên nhân:** Firewall hoặc không có internet

**Giải pháp:**
- Kiểm tra kết nối internet
- Tắt firewall/antivirus tạm thời
- Thử đổi port từ 587 sang 465

### Lỗi 3: "Template not found: emails/meal-notification-email"
**Nguyên nhân:** Email template không tìm thấy

**Giải pháp:**
- Kiểm tra file tồn tại: `src/main/resources/templates/emails/meal-notification-email.html`
- Rebuild project: `mvnw.cmd clean install`

### Lỗi 4: "No users found"
**Nguyên nhân:** Không có user nào trong database hoặc user không có email

**Giải pháp:**
- Kiểm tra database có users không
- Verify users có email field không null

---

## ✅ Checklist Hoàn Thành

### Trước khi chạy production:
- [ ] Đã cập nhật Gmail credentials trong `application.yml`
- [ ] Đã test gửi email thành công
- [ ] Đã verify email content đẹp và đúng
- [ ] Đã sửa lại cron về giờ thật (7h, 11h, 15h, 17h)
- [ ] Đã xóa test controller (nếu có)
- [ ] Đã test với real user data

### Deploy production:
- [ ] Build final version: `mvnw.cmd clean package`
- [ ] File JAR ở: `target/recipe-discovery-0.0.1-SNAPSHOT.jar`
- [ ] Chạy: `java -jar target/recipe-discovery-0.0.1-SNAPSHOT.jar`

---

## 📞 Cần Hỗ Trợ?

Nếu gặp vấn đề, cung cấp thông tin sau:
1. Error message đầy đủ từ console
2. Đã làm đến bước nào
3. Screenshot lỗi (nếu có)

**Good luck! 🚀**
