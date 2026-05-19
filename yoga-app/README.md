# 瑜伽教练管理应用 - Android 开发指南

## 📱 应用概述

瑜伽教练管理应用是一个完整的 Android 应用，帮助瑜伽教练管理学员、课程、销课和退款。

**核心功能：**
- ✅ 添加和管理学员信息
- ✅ 课程购买和价格管理
- ✅ 上课课表设置
- ✅ 销课和请假管理
- ✅ 退课和退款处理
- ✅ 闹钟和提醒通知

## 🏗️ 项目架构

### MVVM 架构

```
UI Layer (Composables)
    ↓
ViewModel (State Management)
    ↓
Repository (Business Logic)
    ↓
DAO (Data Access)
    ↓
Room Database
```

### 项目结构

```
yoga-app/
├── src/main/java/com/yogacoach/app/
│   ├── data/
│   │   ├── entity/          # 数据模型 (5个)
│   │   ├── dao/             # 数据访问接口 (5个)
│   │   ├── database/        # Room数据库配置
│   │   └── repository/      # 业务逻辑仓库 (3个)
│   ├── ui/
│   │   ├── viewmodel/       # 视图模型 (3个)
│   │   ├── screen/          # UI屏幕 (4个)
│   │   └── theme/           # 主题配置
│   ├── service/             # 提醒服务
│   └── broadcast/           # 广播接收器
├── AndroidManifest.xml      # 应用配置
└── build.gradle.kts         # 依赖配置
```

## 📂 核心组件说明

### 1. 数据模型 (data/entity/)

#### Customer (客户)
```kotlin
@Entity
data class Customer(
    @PrimaryKey(autoGenerate = true) val customerId: Long = 0,
    val name: String,           // 姓名
    val age: Int,               // 年龄
    val gender: String,         // 性别
    val courseName: String,     // 课程名称
    val joinDate: LocalDateTime // 加入日期
)
```

#### CoursePackage (课程包)
```kotlin
@Entity
data class CoursePackage(
    @PrimaryKey(autoGenerate = true) val packageId: Long = 0,
    val customerId: Long,
    val pricePerSession: Double,      // 单价
    val totalPrice: Double,            // 总价
    val totalSessions: Int,            // 总课次
    val remainingSessions: Int,        // 剩余课次
    val sessionsPerWeek: Int,          // 每周课次
    val purchaseDate: LocalDateTime    // 购买日期
)
```

#### ClassSchedule (上课安排)
```kotlin
@Entity
data class ClassSchedule(
    @PrimaryKey(autoGenerate = true) val scheduleId: Long = 0,
    val customerId: Long,
    val dayOfWeek: Int,        // 1-7 (周一到周日)
    val startTime: String,     // "09:00"
    val endTime: String,       // "10:30"
    val location: String       // 教室位置
)
```

#### Attendance (考勤)
```kotlin
@Entity
data class Attendance(
    @PrimaryKey(autoGenerate = true) val attendanceId: Long = 0,
    val customerId: Long,
    val packageId: Long,
    val classDateTime: LocalDateTime,
    val status: String,        // "completed", "leave", "absent"
    val notes: String = "",
    val createdAt: LocalDateTime
)
```

#### Refund (退款)
```kotlin
@Entity
data class Refund(
    @PrimaryKey(autoGenerate = true) val refundId: Long = 0,
    val customerId: Long,
    val packageId: Long,
    val refundAmount: Double,
    val reason: String,
    val status: String,        // "pending", "approved", "rejected"
    val requestDate: LocalDateTime,
    val approvalDate: LocalDateTime? = null
)
```

### 2. ViewModel 说明

#### CustomerViewModel
- `addCustomer()` - 添加学员
- `deleteCustomer()` - 删除学员
- `searchCustomers()` - 搜索学员
- `loadCustomerById()` - 加载学员详情
- `getTotalCustomersCount()` - 获取总学员数

#### CourseManagementViewModel
- `purchaseCoursePackage()` - 购买课程
- `setClassSchedule()` - 设置课表
- `markClassAsCompleted()` - 标记已上课 (销课)
- `markClassAsLeave()` - 标记请假
- `markClassAsAbsent()` - 标记缺课
- `loadSessionStats()` - 加载统计信息

#### RefundViewModel
- `requestRefund()` - 申请退课
- `approveRefund()` - 批准退课
- `rejectRefund()` - 拒绝退课
- `calculateRefundAmount()` - 自动计算退款金额

### 3. UI 屏幕说明

#### DashboardScreen (仪表板)
应用主屏幕，显示：
- 学员总数和活跃课程统计
- 快速操作菜单
- 今日课程提醒

#### CustomerListScreen (学员列表)
显示所有学员，支持：
- 学员搜索和过滤
- 快速查看学员信息
- 添加新学员

#### CustomerDetailScreen (学员详情)
显示单个学员的详细信息：
- 客户信息卡片
- 课程标签页 (展示购买的课程)
- 考勤标签页 (上课记录)
- 统计标签页 (数据分析)

#### RefundManagementScreen (退课管理)
管理退课申请和退款：
- 待审批退课列表
- 已批准退课列表
- 已拒绝退课列表
- 退款统计

## 🔄 核心业务流程

### 1. 添加学员
```
DashboardScreen 
  → AddCustomerScreen 
    → CustomerViewModel.addCustomer() 
      → CustomerRepository 
        → CustomerDao.insert()
```

### 2. 购买课程
```
CustomerDetailScreen (课程标签)
  → AddCourseDialog
    → CourseManagementViewModel.purchaseCoursePackage()
      → CourseManagementRepository
        → CoursePackageDao.insert()
```

### 3. 销课流程
```
CustomerDetailScreen (考勤标签)
  → RecordAttendanceDialog
    → CourseManagementViewModel.markClassAsCompleted()
      → AttendanceDao.insert()
      → CoursePackageDao.update() (递减课次)
```

### 4. 请假流程
```
CustomerDetailScreen (考勤标签)
  → RecordAttendanceDialog (选择"请假")
    → CourseManagementViewModel.markClassAsLeave()
      → AttendanceDao.insert()
```

### 5. 退课流程
```
RefundManagementScreen
  → RefundDetailsDialog
    → RefundViewModel.approveRefund()
      → RefundRepository
        → RefundDao.update() (状态改为"approved")
```

## 🚀 使用指南

### 快速开始

1. **克隆项目**
```bash
git clone https://github.com/lijnet/symmetrical-umbrella.git
cd yoga-app
```

2. **打开项目**
   - 使用 Android Studio 打开项目

3. **构建应用**
```bash
./gradlew build
```

4. **运行应用**
   - 在 Android Studio 中点击运行按钮
   - 或使用命令: `./gradlew installDebug`

### 主要功能导航

**仪表板** → 快速查看统计和快速操作

**我的学员** → 管理所有学员
- 搜索学员
- 查看学员详情
- 添加新学员

**学员详情** → 管理单个学员的课程和考勤
- 购买课程
- 记录上课
- 查看统计

**退课管理** → 处理退课申请
- 审批待审批的退课
- 查看已批准/已拒绝的退课

## 🛠️ 开发相关

### 添加新学员

```kotlin
val customer = Customer(
    name = "张三",
    age = 28,
    gender = "男",
    courseName = "瑜伽基础班",
    joinDate = LocalDateTime.now()
)
viewModel.addCustomer(customer)
```

### 购买课程

```kotlin
val coursePackage = CoursePackage(
    customerId = 1L,
    pricePerSession = 100.0,
    totalPrice = 1200.0,
    totalSessions = 12,
    remainingSessions = 12,
    sessionsPerWeek = 2,
    purchaseDate = LocalDateTime.now()
)
courseViewModel.purchaseCoursePackage(coursePackage)
```

### 记录上课

```kotlin
courseViewModel.markClassAsCompleted(
    customerId = 1L,
    packageId = 1L,
    classDateTime = LocalDateTime.now(),
    notes = "表现良好"
)
```

### 申请退课

```kotlin
val refund = Refund(
    customerId = 1L,
    packageId = 1L,
    refundAmount = 400.0,
    reason = "工作繁忙",
    status = "pending",
    requestDate = LocalDateTime.now()
)
refundViewModel.requestRefund(refund)
```

## 📊 数据库设计

### 表关系

```
Customer (1) ---- (N) CoursePackage
Customer (1) ---- (N) ClassSchedule
Customer (1) ---- (N) Attendance
Customer (1) ---- (N) Refund
CoursePackage (1) ---- (N) Attendance
CoursePackage (1) ---- (N) Refund
```

### 查询示例

**获取学员所有课程：**
```kotlin
@Query("SELECT * FROM CoursePackage WHERE customerId = :customerId")
fun getCoursePackagesByCustomer(customerId: Long): Flow<List<CoursePackage>>
```

**获取学员的已上课次数：**
```kotlin
@Query("SELECT COUNT(*) FROM Attendance WHERE customerId = :customerId AND status = 'completed'")
fun getCompletedSessionsCount(customerId: Long): Flow<Int>
```

**获取待审批的退课申请：**
```kotlin
@Query("SELECT * FROM Refund WHERE status = 'pending' ORDER BY requestDate DESC")
fun getPendingRefunds(): Flow<List<Refund>>
```

## 🔐 权限声明

应用需要以下权限 (AndroidManifest.xml):

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

## 📦 依赖

```gradle
// Jetpack
androidx.room:room-runtime:2.6.0
androidx.room:room-ktx:2.6.0
androidx.compose.ui:ui:1.5.0
androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0
androidx.hilt:hilt-navigation-compose:1.1.0

// Hilt (依赖注入)
com.google.dagger:hilt-android:2.47
com.google.dagger:hilt-compiler:2.47

// 日期时间
org.jetbrains.kotlinx:kotlinx-datetime:0.4.0
```

## 🐛 常见问题

**Q: 如何修改课程价格？**
A: 在 CourseDetailScreen 中，点击课程卡片可以编辑课程信息。

**Q: 如何导出学员数据？**
A: 暂未实现，可作为后续功能添加。

**Q: 如何设置多个课表？**
A: 学员可以在 ClassSchedule 中添加多个时间段，系统会显示所有课表。

## 📝 许可证

MIT License

## 👨‍💻 贡献

欢迎提交 Issue 和 Pull Request！

---

**最后更新: 2026-05-19**
