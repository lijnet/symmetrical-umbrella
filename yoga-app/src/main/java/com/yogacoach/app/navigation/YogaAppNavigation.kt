package com.yogacoach.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yogacoach.app.ui.screen.*

/**
 * 导航目的地定义
 */
sealed class NavDestination(val route: String) {
    object Dashboard : NavDestination("dashboard")
    object CustomerList : NavDestination("customer_list")
    object AddCustomer : NavDestination("add_customer")
    object CustomerDetail : NavDestination("customer_detail/{customerId}") {
        fun createRoute(customerId: Long) = "customer_detail/$customerId"
    }
    object CourseManagement : NavDestination("course_management/{customerId}") {
        fun createRoute(customerId: Long) = "course_management/$customerId"
    }
    object RefundManagement : NavDestination("refund_management")
    object ScheduleManagement : NavDestination("schedule_management/{customerId}") {
        fun createRoute(customerId: Long) = "schedule_management/$customerId"
    }
}

/**
 * 应用导航图
 */
@Composable
fun YogaAppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Dashboard.route
    ) {
        composable(NavDestination.Dashboard.route) {
            DashboardScreen(
                onNavigateToCustomers = {
                    navController.navigate(NavDestination.CustomerList.route)
                },
                onNavigateToAddCustomer = {
                    navController.navigate(NavDestination.AddCustomer.route)
                },
                onNavigateToRefunds = {
                    navController.navigate(NavDestination.RefundManagement.route)
                }
            )
        }

        composable(NavDestination.CustomerList.route) {
            CustomerListScreen(
                onBackClick = { navController.popBackStack() },
                onCustomerClick = { customerId ->
                    navController.navigate(NavDestination.CustomerDetail.createRoute(customerId))
                },
                onAddCustomerClick = {
                    navController.navigate(NavDestination.AddCustomer.route)
                }
            )
        }

        composable(NavDestination.AddCustomer.route) {
            AddCustomerScreen(
                onBackClick = { navController.popBackStack() },
                onCustomerAdded = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavDestination.CustomerDetail.route) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")?.toLong() ?: return@composable
            CustomerDetailScreen(
                customerId = customerId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestination.CourseManagement.route) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")?.toLong() ?: return@composable
            CourseManagementScreen(
                customerId = customerId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestination.RefundManagement.route) {
            RefundManagementScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavDestination.ScheduleManagement.route) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")?.toLong() ?: return@composable
            ScheduleManagementScreen(
                customerId = customerId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
