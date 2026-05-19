# 瑜伽应用开发计划

## 已完成 ✅

### 1. 数据库架构
- [x] Customer 实体 (客户信息)
- [x] CoursePackage 实体 (课程包)
- [x] ClassSchedule 实体 (上课安排)
- [x] Attendance 实体 (考勤记录)
- [x] Refund 实体 (退款记录)
- [x] Room Database 配置

### 2. 数据访问层 (DAO)
- [x] CustomerDao
- [x] CoursePackageDao
- [x] ClassScheduleDao
- [x] AttendanceDao
- [x] RefundDao

### 3. 业务逻辑层 (Repository)
- [x] CustomerRepository (客户管理)
- [x] CourseManagementRepository (课程和销课)
- [x] RefundRepository (退课管理)

### 4. 视图模型 (ViewModel)
- [x] CustomerViewModel
- [x] CourseManagementViewModel
- [x] RefundViewModel

### 5. 服务层
- [x] NotificationService (提醒服务)
- [x] ClassReminderReceiver (闹钟广播接收器)
- [x] BootReceiver (启动接收器)

## 待完成 🚧

### 1. UI 界面 (Jetpack Compose)
- [ ] 主界面/仪表板
- [ ] 客户列表界面
- [ ] 添加/编辑客户界面
- [ ] 课程购买界面
- [ ] 销课管理界面
- [ ] 请假管理界面
- [ ] 退课管理界面
- [ ] 财务统计界面

### 2. 功能实现
- [ ] 客户搜索和过滤
- [ ] 课程统计图表
- [ ] 自动计算剩余课次
- [ ] 周期性课表生成
- [ ] 批量导入客户
- [ ] 数据导出 (CSV/PDF)

### 3. 高级功能
- [ ] 云备份
- [ ] 数据同步
- [ ] 多用户支持
- [ ] 权限管理
- [ ] 报告生成

### 4. 优化
- [ ] 性能优化
- [ ] UI/UX 改进
- [ ] 多语言支持
- [ ] 夜间模式
- [ ] 无障碍支持

## 下一步

推荐按以下顺序开发：

1. **UI 框架** - 使用 Jetpack Compose 创建基础页面布局
2. **客户管理** - 实现添加、编辑、删除、查看客户的完整流程
3. **课程管理** - 实现购买课程、设置课表
4. **销课功能** - 实现记录上课和请假
5. **退课功能** - 实现退课申请和退款计算

## 测试清单

- [ ] Unit Tests (业务逻辑)
- [ ] Integration Tests (数据库操作)
- [ ] UI Tests (Compose 界面)
- [ ] 端到端测试

## 部署

- [ ] Google Play Store 上架准备
- [ ] 应用签名配置
- [ ] 隐私政策编写
- [ ] 用户协议编写
