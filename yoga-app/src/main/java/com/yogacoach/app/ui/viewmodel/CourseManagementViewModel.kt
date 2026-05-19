package com.yogacoach.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogacoach.app.data.entity.Attendance
import com.yogacoach.app.data.entity.ClassSchedule
import com.yogacoach.app.data.entity.CoursePackage
import com.yogacoach.app.data.repository.CourseManagementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 课程管理 ViewModel
 * 管理课程、销课、请假等相关的UI状态
 */
@HiltViewModel
class CourseManagementViewModel @Inject constructor(
    private val courseManagementRepository: CourseManagementRepository
) : ViewModel() {

    private val _coursePackages = MutableStateFlow<List<CoursePackage>>(emptyList())
    val coursePackages: StateFlow<List<CoursePackage>> = _coursePackages.asStateFlow()

    private val _classSchedules = MutableStateFlow<List<ClassSchedule>>(emptyList())
    val classSchedules: StateFlow<List<ClassSchedule>> = _classSchedules.asStateFlow()

    private val _attendanceRecords = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceRecords: StateFlow<List<Attendance>> = _attendanceRecords.asStateFlow()

    private val _completedSessions = MutableStateFlow(0)
    val completedSessions: StateFlow<Int> = _completedSessions.asStateFlow()

    private val _leaveSessions = MutableStateFlow(0)
    val leaveSessions: StateFlow<Int> = _leaveSessions.asStateFlow()

    private val _absentSessions = MutableStateFlow(0)
    val absentSessions: StateFlow<Int> = _absentSessions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * 购买课程套餐
     */
    fun purchaseCoursePackage(coursePackage: CoursePackage) {
        viewModelScope.launch {
            try {
                courseManagementRepository.purchaseCoursePackage(coursePackage)
                loadCoursePackages(coursePackage.customerId)
            } catch (e: Exception) {
                _errorMessage.value = "购买课程失败: ${e.message}"
            }
        }
    }

    /**
     * 设置上课课表
     */
    fun setClassSchedule(classSchedule: ClassSchedule) {
        viewModelScope.launch {
            try {
                courseManagementRepository.setClassSchedule(classSchedule)
                loadClassSchedules(classSchedule.customerId)
            } catch (e: Exception) {
                _errorMessage.value = "设置课表失败: ${e.message}"
            }
        }
    }

    /**
     * 加载课程包
     */
    fun loadCoursePackages(customerId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                courseManagementRepository.getCoursePackagesByCustomer(customerId).collect { packages ->
                    _coursePackages.value = packages
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载课程失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 加载课表
     */
    fun loadClassSchedules(customerId: Long) {
        viewModelScope.launch {
            try {
                courseManagementRepository.getSchedulesByCustomer(customerId).collect { schedules ->
                    _classSchedules.value = schedules
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载课表失败: ${e.message}"
            }
        }
    }

    /**
     * 销课 - 记录已上课
     */
    fun markClassAsCompleted(
        customerId: Long,
        packageId: Long,
        classDateTime: LocalDateTime,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                courseManagementRepository.markClassAsCompleted(customerId, packageId, classDateTime, notes)
                loadAttendanceRecords(customerId)
                loadSessionStats(customerId)
            } catch (e: Exception) {
                _errorMessage.value = "销课失败: ${e.message}"
            }
        }
    }

    /**
     * 请假
     */
    fun markClassAsLeave(
        customerId: Long,
        packageId: Long,
        classDateTime: LocalDateTime,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                courseManagementRepository.markClassAsLeave(customerId, packageId, classDateTime, notes)
                loadAttendanceRecords(customerId)
                loadSessionStats(customerId)
            } catch (e: Exception) {
                _errorMessage.value = "请假失败: ${e.message}"
            }
        }
    }

    /**
     * 缺课
     */
    fun markClassAsAbsent(
        customerId: Long,
        packageId: Long,
        classDateTime: LocalDateTime,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                courseManagementRepository.markClassAsAbsent(customerId, packageId, classDateTime, notes)
                loadAttendanceRecords(customerId)
                loadSessionStats(customerId)
            } catch (e: Exception) {
                _errorMessage.value = "标记缺课失败: ${e.message}"
            }
        }
    }

    /**
     * 加载考勤记录
     */
    fun loadAttendanceRecords(customerId: Long) {
        viewModelScope.launch {
            try {
                courseManagementRepository.getAttendanceByCustomer(customerId).collect { records ->
                    _attendanceRecords.value = records
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载考勤记录失败: ${e.message}"
            }
        }
    }

    /**
     * 加载课程统计
     */
    fun loadSessionStats(customerId: Long) {
        viewModelScope.launch {
            try {
                courseManagementRepository.getCompletedSessionsCount(customerId).collect { count ->
                    _completedSessions.value = count
                }
                
                courseManagementRepository.getLeaveSessionsCount(customerId).collect { count ->
                    _leaveSessions.value = count
                }
                
                courseManagementRepository.getAbsentSessionsCount(customerId).collect { count ->
                    _absentSessions.value = count
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载统计失败: ${e.message}"
            }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
