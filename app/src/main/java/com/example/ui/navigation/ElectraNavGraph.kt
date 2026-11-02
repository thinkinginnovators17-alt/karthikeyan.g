package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ElectraAiScreen
import com.example.ui.screens.FirmwareHubScreen
import com.example.ui.screens.HistoricalScreen
import com.example.ui.screens.ImpactScreen
import com.example.ui.screens.LiveTelemetryScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TestHistoryScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GreenNormal
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RedFault
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ElectraViewModel

enum class NavDestination(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
    LIVE("Telemetry", Icons.AutoMirrored.Filled.ShowChart, "nav_live"),
    AI_ASSISTANT("Electra AI", Icons.Default.AutoAwesome, "nav_ai"),
    ALERTS("Alerts", Icons.Default.NotificationsActive, "nav_alerts"),
    HARDWARE("Hardware", Icons.Default.Build, "nav_hardware"),
    TEST_HISTORY("Test History", Icons.Default.Science, "nav_test_history"),
    IMPACT("Quest4Earth", Icons.Default.Eco, "nav_impact"),
    HISTORY("Analytics", Icons.Default.History, "nav_history"),
    REPORTS("Reports", Icons.AutoMirrored.Filled.Assignment, "nav_reports"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectraAppNavigation(
    viewModel: ElectraViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }
    val activeAlerts by viewModel.activeAlerts.collectAsState()

    val bottomNavItems = listOf(
        NavDestination.DASHBOARD,
        NavDestination.LIVE,
        NavDestination.AI_ASSISTANT,
        NavDestination.ALERTS,
        NavDestination.HARDWARE
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest),
        containerColor = NavyDarkest,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentDestination.title,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { currentDestination = NavDestination.IMPACT },
                        modifier = Modifier.testTag("top_nav_impact")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Sustainability & Impact",
                            tint = if (currentDestination == NavDestination.IMPACT) GreenNormal else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { currentDestination = NavDestination.TEST_HISTORY },
                        modifier = Modifier.testTag("top_nav_test_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Test History",
                            tint = if (currentDestination == NavDestination.TEST_HISTORY) ElectricCyan else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { currentDestination = NavDestination.HISTORY },
                        modifier = Modifier.testTag("top_nav_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Historical Analytics",
                            tint = if (currentDestination == NavDestination.HISTORY) ElectricCyan else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { currentDestination = NavDestination.REPORTS },
                        modifier = Modifier.testTag("top_nav_reports")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = "Audit Reports",
                            tint = if (currentDestination == NavDestination.REPORTS) ElectricCyan else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { currentDestination = NavDestination.SETTINGS },
                        modifier = Modifier.testTag("top_nav_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (currentDestination == NavDestination.SETTINGS) ElectricCyan else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyDeep,
                    titleContentColor = TextPrimary
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavyDeep,
                contentColor = TextPrimary,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentDestination == item
                    val alertCount = if (item == NavDestination.ALERTS) activeAlerts.size else 0

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = item },
                        icon = {
                            if (alertCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = RedFault) {
                                            Text(text = alertCount.toString(), color = Color.White, fontSize = 9.sp)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDarkest,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                NavDestination.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToAi = { currentDestination = NavDestination.AI_ASSISTANT },
                    onNavigateToAlerts = { currentDestination = NavDestination.ALERTS },
                    onNavigateToFirmware = { currentDestination = NavDestination.HARDWARE }
                )
                NavDestination.LIVE -> LiveTelemetryScreen(viewModel = viewModel)
                NavDestination.AI_ASSISTANT -> ElectraAiScreen(viewModel = viewModel)
                NavDestination.ALERTS -> AlertsScreen(
                    viewModel = viewModel,
                    onExplainAlertWithAi = { prompt ->
                        viewModel.sendUserChatMessage(prompt)
                        currentDestination = NavDestination.AI_ASSISTANT
                    }
                )
                NavDestination.HARDWARE -> FirmwareHubScreen(viewModel = viewModel)
                NavDestination.TEST_HISTORY -> TestHistoryScreen(viewModel = viewModel)
                NavDestination.IMPACT -> ImpactScreen()
                NavDestination.HISTORY -> HistoricalScreen(viewModel = viewModel)
                NavDestination.REPORTS -> ReportsScreen(viewModel = viewModel)
                NavDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
