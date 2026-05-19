package com.yogacoach.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogacoach.app.data.entity.Customer
import com.yogacoach.app.data.entity.CoursePackage
import com.yogacoach.app.ui.viewmodel.CustomerViewModel
import com.yogacoach.app.ui.viewmodel.CourseManagementViewModel
import java.time.format.DateTimeFormatter

/**
 * ���户详情屏幕
 * 显示客户信息、课程购买、销课、请假、退课等功能
 */
@Composable
fun CustomerDetailScreen(
    customerId: Long,
    customerViewModel: CustomerViewModel = hiltViewModel(),
    courseViewModel: CourseManagementViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val customer by customerViewModel.selectedCustomer.collectAsState()
    val coursePackages by courseViewModel.coursePackages.collectAsState()
    val attendanceRecords by courseViewModel.attendanceRecords.collectAsState()
    val completedSessions by courseViewModel.completedSessions.collectAsState()
    val leaveSessions by courseViewModel.leaveSessions.collectAsState()
    val absentSessions by courseViewModel.absentSessions.collectAsState()

    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showRecordAttendanceDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(customerId) {
        customerViewModel.loadCustomerById(customerId)
        courseViewModel.loadCoursePackages(customerId)
        courseViewModel.loadAttendanceRecords(customerId)
        courseViewModel.loadSessionStats(customerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("客户详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 客户基本信息卡片
            customer?.let { cust ->
                CustomerInfoCard(customer = cust)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 标签页
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("课程") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("考勤") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("统计") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> CourseTab(
                    coursePackages = coursePackages,
                    onAddCourse = { showAddCourseDialog = true }
                )
                1 -> AttendanceTab(
                    attendanceRecords = attendanceRecords,
                    onRecordAttendance = { showRecordAttendanceDialog = true }
                )
                2 -> StatisticsTab(
                    completedSessions = completedSessions,
                    leaveSessions = leaveSessions,
                    absentSessions = absentSessions,
                    totalSessions = coursePackages.sumOf { it.totalSessions }
                )
            }
        }
    }

    if (showAddCourseDialog && customer != null) {
        AddCourseDialog(
            customerId = customer!!.customerId,
            onDismiss = { showAddCourseDialog = false },
            onConfirm = { coursePackage ->
                courseViewModel.purchaseCoursePackage(coursePackage)
                showAddCourseDialog = false
            }
        )
    }

    if (showRecordAttendanceDialog && customer != null) {
        RecordAttendanceDialog(
            customerId = customer!!.customerId,
            onDismiss = { showRecordAttendanceDialog = false },
            onConfirm = { status, notes ->
                // 根据状态调用相应的方法
                showRecordAttendanceDialog = false
            }
        )
    }
}

@Composable
fun CustomerInfoCard(customer: Customer) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = customer.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "课程: ${customer.courseName}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "客户",
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF6200EE)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(label = "年龄", value = "${customer.age}岁")
                InfoItem(label = "性别", value = customer.gender)
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CourseTab(
    coursePackages: List<CoursePackage>,
    onAddCourse: () -> Unit
) {
    LazyColumn {
        items(coursePackages) { pkg ->
            CoursePackageCard(coursePackage = pkg)
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddCourse,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加课程")
                Spacer(modifier = Modifier.width(8.dp))
                Text("购买新课程")
            }
        }
    }
}

@Composable
fun CoursePackageCard(coursePackage: CoursePackage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "单价: ¥${coursePackage.pricePerSession}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "总价: ¥${coursePackage.totalPrice}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${coursePackage.totalSessions}课次",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "每周${coursePackage.sessionsPerWeek}次",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (coursePackage.totalSessions - coursePackage.remainingSessions).toFloat() / coursePackage.totalSessions },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                trackColor = Color.LightGray,
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "已上: ${coursePackage.totalSessions - coursePackage.remainingSessions}/${coursePackage.totalSessions}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun AttendanceTab(
    attendanceRecords: List<Attendance>,
    onRecordAttendance: () -> Unit
) {
    LazyColumn {
        items(attendanceRecords) { record ->
            AttendanceRecordCard(attendance = record)
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRecordAttendance,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = "记录")
                Spacer(modifier = Modifier.width(8.dp))
                Text("记录上课")
            }
        }
    }
}

@Composable
fun AttendanceRecordCard(attendance: Attendance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = attendance.classDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = attendance.notes.ifEmpty { "无备注" },
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            StatusBadge(status = attendance.status)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status) {
        "completed" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "已上课")
        "leave" -> Triple(Color(0xFFFFF3E0), Color(0xFFF57F17), "请假")
        "absent" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "缺课")
        else -> Triple(Color.LightGray, Color.Gray, "未知")
    }
    
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatisticsTab(
    completedSessions: Int,
    leaveSessions: Int,
    absentSessions: Int,
    totalSessions: Int
) {
    LazyColumn {
        item {
            StatisticsCard(
                label = "已上课",
                value = completedSessions,
                total = totalSessions,
                color = Color(0xFF4CAF50)
            )
        }
        item {
            StatisticsCard(
                label = "请假次数",
                value = leaveSessions,
                total = totalSessions,
                color = Color(0xFFFFC107)
            )
        }
        item {
            StatisticsCard(
                label = "缺课次数",
                value = absentSessions,
                total = totalSessions,
                color = Color(0xFFF44336)
            )
        }
    }
}

@Composable
fun StatisticsCard(
    label: String,
    value: Int,
    total: Int,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = label, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$value / $total",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(60.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "${if (total > 0) (value * 100 / total) else 0}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun AddCourseDialog(
    customerId: Long,
    onDismiss: () -> Unit,
    onConfirm: (CoursePackage) -> Unit
) {
    var pricePerSession by remember { mutableStateOf("") }
    var totalPrice by remember { mutableStateOf("") }
    var totalSessions by remember { mutableStateOf("") }
    var sessionsPerWeek by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("购买新课程") },
        text = {
            Column {
                OutlinedTextField(
                    value = pricePerSession,
                    onValueChange = { pricePerSession = it },
                    label = { Text("单价 (¥)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = totalPrice,
                    onValueChange = { totalPrice = it },
                    label = { Text("总价 (¥)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = totalSessions,
                    onValueChange = { totalSessions = it },
                    label = { Text("总课次") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = sessionsPerWeek,
                    onValueChange = { sessionsPerWeek = it },
                    label = { Text("每周课次") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pricePerSession.isNotEmpty() && totalPrice.isNotEmpty() &&
                        totalSessions.isNotEmpty() && sessionsPerWeek.isNotEmpty()
                    ) {
                        val coursePackage = CoursePackage(
                            packageId = 0,
                            customerId = customerId,
                            pricePerSession = pricePerSession.toDouble(),
                            totalPrice = totalPrice.toDouble(),
                            totalSessions = totalSessions.toInt(),
                            remainingSessions = totalSessions.toInt(),
                            sessionsPerWeek = sessionsPerWeek.toInt(),
                            purchaseDate = java.time.LocalDateTime.now()
                        )
                        onConfirm(coursePackage)
                    }
                }
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun RecordAttendanceDialog(
    customerId: Long,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("completed") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录出勤") },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "completed" to "已上课",
                        "leave" to "请假",
                        "absent" to "缺课"
                    ).forEach { (value, label) ->
                        FilterChip(
                            selected = selectedStatus == value,
                            onClick = { selectedStatus = value },
                            label = { Text(label) }
                        )
                    }
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("备注") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedStatus, notes) }) {
                Text("确认")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

// 缺少的导入数据类
data class Attendance(
    val attendanceId: Long = 0,
    val customerId: Long = 0,
    val packageId: Long = 0,
    val classDateTime: java.time.LocalDateTime = java.time.LocalDateTime.now(),
    val status: String = "completed", // completed, leave, absent
    val notes: String = "",
    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
)
