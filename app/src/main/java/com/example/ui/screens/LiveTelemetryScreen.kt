package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FreshnessBadge
import com.example.ui.components.TelemetryWaveformChart
import com.example.ui.components.WaitingForEsp32Card
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldElectric
import com.example.ui.theme.GreenNormal
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.OrangeWarning
import com.example.ui.theme.RedFault
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.ElectraViewModel

@Composable
fun LiveTelemetryScreen(
    viewModel: ElectraViewModel,
    modifier: Modifier = Modifier
) {
    val reading by viewModel.currentReading.collectAsState()
    val recentReadings by viewModel.recentReadings.collectAsState()
    val settings by viewModel.deviceSettings.collectAsState()

    val currentPoints = recentReadings.map { it.currentA }.reversed()
    val voltagePoints = recentReadings.map { it.voltageV }.reversed()
    val powerPoints = recentReadings.map { it.calculatedPower }.reversed()
    val tempPoints = recentReadings.map { it.temperatureC }.reversed()
    val vibrationPoints = recentReadings.map { it.vibrationMagnitude }.reversed()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("live_telemetry_screen")
    ) {
        Text(
            text = "Live Sensor Telemetry",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "High-frequency real-time waveforms & sensor health diagnostics",
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (reading == null) {
            WaitingForEsp32Card(
                ipAddress = settings.esp32IpAddress,
                onRetryConnect = { viewModel.startMonitoring() }
            )
            Spacer(modifier = Modifier.height(14.dp))
        } else {
            // Stream metadata banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
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
                            text = "SEQUENCE: #${reading!!.sequenceNumber}",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "FIRMWARE: ${reading!!.firmwareVersion}",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                    FreshnessBadge(timestamp = reading!!.timestamp, isStale = reading!!.isStale)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Sensor Health Diagnostics Matrix
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "HARDWARE SENSOR DIAGNOSTIC MATRIX",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SensorStatusPill(name = "ACS712 (GPIO34)", isOk = true)
                    SensorStatusPill(name = "Voltage (GPIO35)", isOk = true)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val dhtOk = reading?.sensorHealth?.contains("DHT_ERR") != true
                    val mpuOk = reading?.sensorHealth?.contains("MPU_ERR") != true
                    SensorStatusPill(name = "DHT11 (GPIO4)", isOk = dhtOk)
                    SensorStatusPill(name = "MPU6050 (I2C 21/22)", isOk = mpuOk)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Current Waveform
        TelemetryWaveformChart(
            title = "Current Waveform (ACS712)",
            dataPoints = if (currentPoints.isEmpty()) listOf(0f, 0f) else currentPoints,
            unit = "A",
            lineColor = ElectricCyan,
            maxY = settings.overcurrentThresholdA * 1.15f
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Voltage Waveform
        TelemetryWaveformChart(
            title = "Line Voltage (Voltage Divider)",
            dataPoints = if (voltagePoints.isEmpty()) listOf(0f, 0f) else voltagePoints,
            unit = "V",
            lineColor = GoldElectric,
            maxY = settings.overvoltageThresholdV * 1.1f
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Power Waveform
        TelemetryWaveformChart(
            title = "Active Power (Watts)",
            dataPoints = if (powerPoints.isEmpty()) listOf(0f, 0f) else powerPoints,
            unit = "W",
            lineColor = GreenNormal
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Vibration Waveform
        TelemetryWaveformChart(
            title = "Mechanical Vibration Magnitude (MPU6050)",
            dataPoints = if (vibrationPoints.isEmpty()) listOf(1f, 1f) else vibrationPoints,
            unit = "g",
            lineColor = OrangeWarning,
            maxY = settings.vibrationThresholdG * 1.3f
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Temperature Waveform
        TelemetryWaveformChart(
            title = "Enclosure Temperature (DHT11)",
            dataPoints = if (tempPoints.isEmpty()) listOf(25f, 25f) else tempPoints,
            unit = "°C",
            lineColor = Color(0xFFF43F5E),
            minY = 20f,
            maxY = 50f
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun SensorStatusPill(name: String, isOk: Boolean) {
    Row(
        modifier = Modifier
            .background(NavyDeep, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(if (isOk) GreenNormal else RedFault, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = name,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (isOk) "OK" else "ERR",
            color = if (isOk) GreenNormal else RedFault,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
