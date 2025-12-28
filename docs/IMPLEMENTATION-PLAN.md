# Implementation Plan: Email Notification System cho Meal Plan

## Mục tiêu

Tích hợp hệ thống gửi email thông báo tự động cho users về meal plan của họ. Hệ thống sẽ gửi email theo **4 khung giờ** trong ngày:
1. **Bữa sáng** (BREAKFAST): 07:00
2. **Bữa trưa** (LUNCH): 11:00  
3. **Bữa chiều** (SNACK): 15:00
4. **Bữa tối** (DINNER): 17:00

Email chỉ gửi cho các users có:
- Email đã đăng ký trong hệ thống
- Có meal plan cho ngày hôm đó
- Có món ăn được lên lịch cho bữa tương ứng

---

## User Review Required

> [!IMPORTANT]
> **Xác nhận khung giờ gửi email**
> - Bữa sáng: 07:00 AM
> - Bữa trưa: 11:00 AM
> - Bữa chiều: 03:00 PM (15:00)
> - Bữa tối: 05:00 PM (17:00)
> 
> Nếu bạn muốn thay đổi giờ gửi, vui lòng cho tôi biết!

> [!WARNING]
> **Email Configuration**
> - Hiện tại trong `application.yml` có cấu hình Gmail nhưng chưa điền thông tin thực
> - Bạn cần cập nhật: `spring.mail.username` và `spring.mail.password` (App Password)
> - Nếu không điền, email sẽ không gửi được (sẽ có error trong log)

> [!NOTE]
> **Timezone**
> - Scheduler sẽ chạy theo timezone của server
> - Hiện tại code sẽ dùng timezone mặc định của JVM
> - Có thể config thêm timezone nếu cần (ví dụ: Asia/Ho_Chi_Minh)

---

## Proposed Changes

### Component 1: Email Service

#### [NEW] [EmailService.java](file:///c:/Users/ASUS%20TUF%20GAMING/IdeaProjects/Project-SpringBoot-Sprint_2/src/main/java/com/example/recipediscovery/service/EmailService.java)

Tạo service mới để xử lý gửi email:
- Inject `JavaMailSender` (đã có sẵn trong Spring Boot với mail starter)
- Method `sendMealPlanNotification(User user, MealPlanItem item, String mealType)`
- Tạo email body HTML với thông tin món ăn:  
  - Tên món
  - Hình ảnh
  - Nguyên liệu
  - Hướng dẫn nấu
  - Calories, cooking time
- Handle exceptions và log errors

---

### Component 2: Scheduler Service

#### [NEW] [MealPlanNotificationScheduler.java](file:///c:/Users/ASUS%20TUF%20GAMING/IdeaProjects/Project-SpringBoot-Sprint_2/src/main/java/com/example/recipediscovery/scheduler/MealPlanNotificationScheduler.java)

Tạo scheduler với 4 cron jobs:
- `@Scheduled(cron = "0 0 7 * * ?")` - Breakfast notification  
- `@Scheduled(cron = "0 0 11 * * ?")` - Lunch notification
- `@Scheduled(cron = "0 0 15 * * ?")` - Snack notification
- `@Scheduled(cron = "0 0 17 * * ?")` - Dinner notification

Logic cho mỗi scheduled method:
1. Lấy tất cả users trong hệ thống
2. Filter users có email (not null, not empty)
3. Với mỗi user:
   - Lấy meal plan của tuần hiện tại
   - Xác định day of week hôm nay (1=Monday...7=Sunday)
   - Tìm meal plan item tương ứng với day + meal type
   - Nếu có món: gửi email notification
4. Log kết quả (số email đã gửi)

Dependencies:
- `UserRepository` - lấy all users
- `MealPlanRepository` - lấy plan theo user và week
- `MealPlanItemRepository` - lấy items của plan
- `EmailService` - gửi email

---

### Component 3: Main Application Config

#### [MODIFY] [RecipeDiscoveryApplication.java](file:///c:/Users/ASUS%20TUF%20GAMING/IdeaProjects/Project-SpringBoot-Sprint_2/src/main/java/com/example/recipediscovery/RecipeDiscoveryApplication.java)

Thêm annotation `@EnableScheduling` để kích hoạt scheduled tasks

```java
@SpringBootApplication
@EnableScheduling  // ← Thêm dòng này
public class RecipeDiscoveryApplication {
    // ...
}
```

---

### Component 4: Email Template

#### [NEW] [meal-notification-email.html](file:///c:/Users/ASUS%20TUF%20GAMING/IdeaProjects/Project-SpringBoot-Sprint_2/src/main/resources/templates/emails/meal-notification-email.html)

Tạo HTML email template (Thymeleaf) với:
- Header với logo/branding
- Thông tin bữa ăn:
  - Icon + tên bữa ăn
  - Thời gian gợi ý
- Thông tin món ăn:
  - Hình ảnh món  
  - Tên món
  - Calories & cooking time
  - Nguyên liệu (danh sách)
  - Hướng dẫn nấu (từng bước)
- Footer với link đến app
- Responsive design cho mobile

---

### Component 5: Repository Enhancements

#### [MODIFY] [UserRepository.java](file:///c:/Users/ASUS%20TUF%20GAMING/IdeaProjects/Project-SpringBoot-Sprint_2/src/main/java/com/example/recipediscovery/repository/UserRepository.java)

Thêm query method để lấy users có email:

```java
// Lấy tất cả users có email (không null và không empty)
@Query("SELECT u FROM User u WHERE u.email IS NOT NULL AND u.email != ''")
List<User> findAllWithEmail();
```

#### [CHECK] [MealPlanRepository.java](file:///c:/Users/ASUS%20TUF%20GAMING/IdeaProjects/Project-SpringBoot-Sprint_2/src/main/java/com/example/recipediscovery/repository/MealPlanRepository.java)

Đã có method `findByUserIdAndWeekStartDate` - không cần thay đổi

#### [CHECK] [MealPlanItemRepository.java](file:///c:/Users/ASUS%20TUF%20GAMING/IdeaProjects/Project-SpringBoot-Sprint_2/src/main/java/com/example/recipediscovery/repository/MealPlanItemRepository.java)

Cần verify có method để query theo meal plan, day of week và meal type

---

### Component 6: Dependencies

#### [MODIFY] [pom.xml](file:///c:/Users/ASUS%20TUF%20GAMING/IdeaProjects/Project-SpringBoot-Sprint_2/pom.xml)

Kiểm tra và thêm dependency nếu chưa có:

```xml
<!-- Spring Mail - đã có sẵn nhưng cần verify -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

*Lưu ý: Thực ra dependency này đã được comment trong application.yml nên có thể đã được thêm rồi, cần kiểm tra lại*

---

## Verification Plan

### Automated Tests

Không có unit tests hiện tại trong project. Sẽ không thêm unit tests trong scope này để tránh làm phức tạp.

### Manual Verification

#### 1. **Test Email Service Functionality**

**Prerequisites**:
- Cập nhật `application.yml` với Gmail credentials thực:
  ```yaml
  spring:
    mail:
      username: your-email@gmail.com  # Email thật của bạn
      password: your-app-password      # App Password từ Google
  ```

**Steps**:
1. Tạo một test endpoint tạm (hoặc dùng console)
2. Gọi `EmailService.sendMealPlanNotification()` với data test
3. Kiểm tra email inbox xem có nhận được email không
4. Verify nội dung email hiển thị đúng
5. Kiểm tra hiển thị trên mobile và desktop

**Expected Result**: Email gửi thành công, nội dung hiển thị đẹp và đầy đủ thông tin

---

#### 2. **Test Scheduler Timing**

> [!CAUTION]
> Để test scheduler, bạn có 2 options:

**Option A - Test với thời gian thực (khuyến nghị)**:
1. Sửa tạm cron expression để chạy sau vài phút (ví dụ: `0 */2 * * * ?` để chạy mỗi 2 phút)
2. Khởi động application
3. Chờ đến thời điểm scheduled
4. Kiểm tra console log
5. Kiểm tra email inbox

**Option B - Test ngay lập tức**:
1. Comment @Scheduled annotations
2. Tạo REST endpoint gọi thẳng method notification
3. Test qua Postman/browser
4. Sau khi test xong, uncomment @Scheduled

**Steps cho Option A**:
```java
// MealPlanNotificationScheduler.java
// Sửa tạm thành:
@Scheduled(cron = "0 */2 * * * ?")  // Chạy mỗi 2 phút
public void sendBreakfastNotifications() {
    logger.info("🍳 [TEST] Running breakfast notifications at: " + LocalDateTime.now());
    // ...
}
```

1. Build và run application: `mvn spring-boot:run`
2. Chờ 2 phút
3. Kiểm tra console log có dòng "🍳 [TEST] Running..."
4. Kiểm tra inbox có email mới
5. **Sau khi test xong, nhớ sửa lại cron về giờ thực**

**Expected Result**: Scheduler chạy đúng thời gian, gửi email cho đúng users

---

#### 3. **Test Business Logic**

**Setup**:
1. Đảm bảo có ít nhất 2 users trong database có email
2. User 1: Có meal plan với món cho bữa sáng ngày hôm nay
3. User 2: Không có meal plan hoặc không có món cho bữa sáng

**Steps**:
1. Trigger breakfast notification (manual hoặc đợi scheduler)
2. Kiểm tra console log
3. Verify User 1 nhận được email
4. Verify User 2 KHÔNG nhận được email

**Expected Result**: 
- Console log: "Sent 1 breakfast notifications"
- Chỉ User 1 nhận email
- Email chứa đúng thông tin món ăn của User 1

---

#### 4. **Test Edge Cases**

**Test cases**:
- [ ] User có email null → Không gửi, không crash
- [ ] User có email empty string → Không gửi, không crash  
- [ ] MealPlanItem có recipe null → Không gửi (món đã bị xóa)
- [ ] Recipe có imageUrl null → Email vẫn gửi nhưng dùng placeholder image
- [ ] Gmail credentials sai → Log error nhưng app không crash

**Steps**: 
1. Setup từng test case trong database
2. Trigger notification
3. Kiểm tra log và inbox

**Expected Result**: App xử lý gracefully, không crash, log errors rõ ràng

---

#### 5. **Test Timezone Correctness**

**Steps**:
1. Kiểm tra server timezone: `System.out.println(ZoneId.systemDefault())`
2. Set cron chạy sau 1 phút
3. Ghi chú thời gian hiện tại
4. Đợi notification chạy
5. So sánh thời gian thực tế với kỳ vọng

**Expected Result**: Notification chạy đúng giờ theo timezone server

---

## Implementation Notes

**Thứ tự thực hiện**:
1. ✅ Enable scheduling trong main application
2. ✅ Thêm dependency email (nếu chưa có)
3. ✅ Tạo email template HTML
4. ✅ Implement EmailService
5. ✅ Implement MealPlanNotificationScheduler
6. ✅ Cập nhật repositories (nếu cần)
7. ✅ Test thủ công theo verification plan
8. ✅ Điều chỉnh dựa trên kết quả test
9. ✅ Deploy và monitor

**Logging Strategy**:
- INFO: Số lượng email đã gửi mỗi batch
- DEBUG: Chi tiết từng user được process
- ERROR: Lỗi khi gửi email, connection issues
- Không log sensitive data (email addresses, user data)

**Performance Considerations**:
- Nếu có >1000 users: Consider batch processing
- Add retry logic cho failed emails
- Rate limiting nếu Gmail có giới hạn

**Future Enhancements** (out of scope):
- User preference: Cho phép user bật/tắt notifications
- Custom notification time per user
- Digest email (gửi 1 email cho cả ngày thay vì 4 emails)
- SMS notifications
- Push notifications
