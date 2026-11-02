package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.data.model.LineStatus
import com.example.ui.components.ConnectionBadge
import com.example.ui.components.FreshnessBadge
import com.example.ui.components.LineStatusBadge
import com.example.ui.components.SafetyNoticeBanner
import com.example.ui.components.TelemetryMetricCard
import com.example.ui.components.TelemetryWaveformChart
import com.example.ui.components.VibrationMetricCard
import com.example.ui.components.WaitingForEsp32Card
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldElectric
import com.example.ui.theme.GreenNormal
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.OrangeWarning
import com.example.ui.theme.PurpleAnalysis
import com.example.ui.theme.RedFault
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.ElectraViewModel

@Composable
fun DashboardScreen(
    viewModel: ElectraViewModel,
    onNavigateToAi: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToFirmware: () -> Unit,
    modifier: Modifier = Modifier
) {
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val reading by viewModel.currentReading.collectAsState()
    val lineStatus by viewModel.lineStatus.collectAsState()
    val wireStatus by viewModel.wireStatus.collectAsState()
    val settings by viewModel.deviceSettings.collectAsState()
    val recentReadings by viewModel.recentReadings.collectAsState()
    val activeAlerts by viewModel.activeAlerts.collectAsState()
    val aiSummary by viewModel.aiQuickSummary.collectAsState()

    var showLabInjector by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("dashboard_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "ELECTRA SENSE AI",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "System Node: ${settings.deviceName}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                ConnectionBadge(status = connectionStatus)
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = { viewModel.startMonitoring() },
                    modifier = Modifier.testTag("refresh_connection_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Stream",
                        tint = ElectricCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Safety Notice Banner
        SafetyNoticeBanner()

        Spacer(modifier = Modifier.height(14.dp))

        // Electrical Line Status Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = when (lineStatus) {
                        LineStatus.NORMAL -> GreenNormal.copy(alpha = 0.5f)
                        LineStatus.WARNING -> OrangeWarning.copy(alpha = 0.6f)
                        LineStatus.POSSIBLE_FAULT -> RedFault.copy(alpha = 0.8f)
                        LineStatus.CALIBRATION_REQUIRED -> GoldElectric.copy(alpha = 0.6f)
                    },
                    shape = RoundedCornerShape(14.dp)
                )
                .testTag("line_status_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when (lineStatus) {
                                        LineStatus.NORMAL -> GreenNormal.copy(alpha = 0.15f)
                                        LineStatus.WARNING -> OrangeWarning.copy(alpha = 0.15f)
                                        LineStatus.POSSIBLE_FAULT -> RedFault.copy(alpha = 0.15f)
                                        LineStatus.CALIBRATION_REQUIRED -> GoldElectric.copy(alpha = 0.15f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (lineStatus) {
                                    LineStatus.NORMAL -> Icons.Default.CheckCircle
                                    LineStatus.WARNING, LineStatus.POSSIBLE_FAULT -> Icons.Default.Warning
                                    LineStatus.CALIBRATION_REQUIRED -> Icons.Default.Bolt
                                },
                                contentDescription = null,
                                tint = when (lineStatus) {
                                    LineStatus.NORMAL -> GreenNormal
                                    LineStatus.WARNING -> OrangeWarning
                                    LineStatus.POSSIBLE_FAULT -> RedFault
                                    LineStatus.CALIBRATION_REQUIRED -> GoldElectric
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ELECTRICAL LINE STATUS",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = lineStatus.displayName.uppercase(),
                                color = when (lineStatus) {
                                    LineStatus.NORMAL -> GreenNormal
                                    LineStatus.WARNING -> OrangeWarning
                                    LineStatus.POSSIBLE_FAULT -> RedFault
                                    LineStatus.CALIBRATION_REQUIRED -> GoldElectric
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    if (reading != null) {
                        FreshnessBadge(timestamp = reading!!.timestamp, isStale = reading!!.isStale)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lineStatus.description,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Wire Monitoring Status Card (Quest4Earth Wire State)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = when (wireStatus) {
                        com.example.data.model.WireMonitoringStatus.NORMAL -> GreenNormal.copy(alpha = 0.5f)
                        com.example.data.model.WireMonitoringStatus.BREAK_SUSPECTED -> OrangeWarning.copy(alpha = 0.6f)
                        com.example.data.model.WireMonitoringStatus.FAULT_DETECTED -> RedFault.copy(alpha = 0.8f)
                        com.example.data.model.WireMonitoringStatus.SENSOR_UNAVAILABLE -> TextTertiary.copy(alpha = 0.5f)
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .testTag("wire_monitoring_card"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDeep)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = when (wireStatus) {
                                com.example.data.model.WireMonitoringStatus.NORMAL -> GreenNormal
                                com.example.data.model.WireMonitoringStatus.BREAK_SUSPECTED -> OrangeWarning
                                com.example.data.model.WireMonitoringStatus.FAULT_DETECTED -> RedFault
                                com.example.data.model.WireMonitoringStatus.SENSOR_UNAVAILABLE -> TextSecondary
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WIRE CONTINUITY MONITOR",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }

                    Text(
                        text = wireStatus.displayName,
                        color = when (wireStatus) {
                            com.example.data.model.WireMonitoringStatus.NORMAL -> GreenNormal
                            com.example.data.model.WireMonitoringStatus.BREAK_SUSPECTED -> OrangeWarning
                            com.example.data.model.WireMonitoringStatus.FAULT_DETECTED -> RedFault
                            com.example.data.model.WireMonitoringStatus.SENSOR_UNAVAILABLE -> TextSecondary
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = wireStatus.description,
                    color = TextTertiary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // If no reading or disconnected, show explicit waiting card
        if (reading == null) {
            WaitingForEsp32Card(
                ipAddress = settings.esp32IpAddress,
                onRetryConnect = { viewModel.startMonitoring() }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Live Telemetry Grid
        val r = reading
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Current Metric
            TelemetryMetricCard(
                title = "Current",
                value = r?.let { String.format("%.2f", it.currentA) } ?: "--",
                unit = "A",
                icon = Icons.Default.ElectricMeter,
                accentColor = ElectricCyan,
                progressFraction = r?.let { it.currentA / settings.overcurrentThresholdA },
                subText = "Limit: ${settings.overcurrentThresholdA}A (ACS712 ${settings.acs712Variant})",
                isWarning = (r?.currentA ?: 0f) > settings.overcurrentThresholdA,
                modifier = Modifier.weight(1f)
            )

            // Voltage Metric
            TelemetryMetricCard(
                title = "Voltage",
                value = r?.let { String.format("%.1f", it.voltageV) } ?: "--",
                unit = "V",
                icon = Icons.Default.Bolt,
                accentColor = GoldElectric,
                progressFraction = r?.let { it.voltageV / settings.overvoltageThresholdV },
                subText = "Range: ${settings.undervoltageThresholdV}V - ${settings.overvoltageThresholdV}V",
                isWarning = (r?.voltageV ?: 0f) > settings.overvoltageThresholdV || ((r?.currentA ?: 0f) > 0.1f && (r?.voltageV ?: 0f) < settings.undervoltageThresholdV),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Calculated Power Metric
            val isPowerCalculated = r?.isPowerValid == true
            TelemetryMetricCard(
                title = "Calculated Power",
                value = if (isPowerCalculated) String.format("%.1f", r!!.calculatedPower) else "N/A",
                unit = "W",
                icon = Icons.Default.Speed,
                accentColor = GreenNormal,
                subText = if (isPowerCalculated) "Active Load (V × I)" else "Requires validated V & I",
                modifier = Modifier.weight(1f)
            )

            // Temperature Metric
            TelemetryMetricCard(
                title = "Enclosure Temp",
                value = r?.let { String.format("%.1f", it.temperatureC) } ?: "--",
                unit = "°C",
                icon = Icons.Default.Thermostat,
                accentColor = OrangeWarning,
                progressFraction = r?.let { it.temperatureC / settings.highTempThresholdC },
                subText = "DHT11 Sensor (GPIO4)",
                isWarning = (r?.temperatureC ?: 0f) > settings.highTempThresholdC,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Humidity Metric
            TelemetryMetricCard(
                title = "Humidity",
                value = r?.let { String.format("%.1f", it.humidityPercent) } ?: "--",
                unit = "%",
                icon = Icons.Default.WaterDrop,
                accentColor = Color(0xFF38BDF8),
                subText = "Ambient Relative Humidity",
                modifier = Modifier.weight(1f)
            )

            // Vibration Card
            VibrationMetricCard(
                mag = r?.vibrationMagnitude ?: 1.0f,
                x = r?.vibrationX ?: 0f,
                y = r?.vibrationY ?: 0f,
                z = r?.vibrationZ ?: 1.0f,
                threshold = settings.vibrationThresholdG,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Current Waveform Chart
        val currentPoints = recentReadings.map { it.currentA }.reversed()
        TelemetryWaveformChart(
            title = "Current Load Trend (Amperes)",
            dataPoints = if (currentPoints.isEmpty()) listOf(0f, 0f) else currentPoints,
            unit = "A",
            lineColor = ElectricCyan,
            maxY = settings.overcurrentThresholdA * 1.1f
        )

        Spacer(modifier = Modifier.height(14.dp))

        // AI Electrical Assistant Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PurpleAnalysis.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .clickable { onNavigateToAi() }
                .testTag("ai_summary_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(PurpleAnalysis.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = PurpleAnalysis,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ELECTRA AI ASSISTANT",
                            color = PurpleAnalysis,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }
                    Text(
                        text = "Open Chat →",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = aiSummary,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Recent Alerts Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                .testTag("recent_alerts_preview"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alerts",
                            tint = if (activeAlerts.isNotEmpty()) RedFault else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACTIVE ALERTS (${activeAlerts.size})",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "View All →",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToAlerts() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (activeAlerts.isEmpty()) {
                    Text(
                        text = "No active faults or threshold violations. System operating normally.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                } else {
                    activeAlerts.take(2).forEach { alert ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(RedFault, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alert.title,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = alert.message,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                            Button(
                                onClick = { viewModel.acknowledgeAlert(alert.id) },
                                modifier = Modifier.height(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(text = "Ack", color = ElectricCyan, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lab Verification & Safety Demonstration Tools
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Educational Laboratory Tools",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (showLabInjector) "Hide Lab Controls" else "Show Lab Controls",
                color = ElectricCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { showLabInjector = !showLabInjector }
            )
        }

        if (showLabInjector) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldElectric.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = NavyDeep)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔬 BENCH TEST INJECTION (Educational Mode)",
                        color = GoldElectric,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Allows students and evaluators to verify alert triggers and AI diagnosis safely without hazardous high-voltage test equipment.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.injectLabVerification(
                                    currentA = 1.25f,
                                    voltageV = 12.0f,
                                    tempC = 27.0f,
                                    humPct = 48f,
                                    vibrationMag = 1.02f
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenNormal.copy(alpha = 0.2f)),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GreenNormal))
                        ) {
                            Text("Normal (12V/1.2A)", color = GreenNormal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.injectLabVerification(
                                    currentA = 4.8f,
                                    voltageV = 11.2f,
                                    tempC = 38.5f,
                                    humPct = 52f,
                                    vibrationMag = 1.15f
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = RedFault.copy(alpha = 0.2f)),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(RedFault))
                        ) {
                            Text("Overcurrent (4.8A)", color = RedFault, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.injectLabVerification(
                                    currentA = 2.1f,
                                    voltageV = 6.4f,
                                    tempC = 28.0f,
                                    humPct = 45f,
                                    vibrationMag = 1.08f
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeWarning.copy(alpha = 0.2f)),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(OrangeWarning))
                        ) {
                            Text("Voltage Sag (6.4V)", color = OrangeWarning, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.injectLabVerification(
                                    currentA = 1.8f,
                                    voltageV = 12.0f,
                                    tempC = 31.0f,
                                    humPct = 50f,
                                    vibrationMag = 2.65f
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldElectric.copy(alpha = 0.2f)),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GoldElectric))
                        ) {
                            Text("High Vibration (2.6g)", color = GoldElectric, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
