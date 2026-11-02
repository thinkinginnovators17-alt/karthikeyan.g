package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SensorReading
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldElectric
import com.example.ui.theme.GreenNormal
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RedFault
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.ElectraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoricalScreen(
    viewModel: ElectraViewModel,
    modifier: Modifier = Modifier
) {
    val recentReadings by viewModel.recentReadings.collectAsState()
    val context = LocalContext.current

    var selectedWindowIndex by remember { mutableStateOf(0) }
    val windowLabels = listOf("1 Hour", "24 Hours", "7 Days", "30 Days")

    val currents = recentReadings.map { it.currentA }
    val voltages = recentReadings.map { it.voltageV }
    val powers = recentReadings.map { it.calculatedPower }
    val temps = recentReadings.map { it.temperatureC }

    val peakCurrent = currents.maxOrNull() ?: 0f
    val avgCurrent = if (currents.isNotEmpty()) currents.average().toFloat() else 0f
    val peakVoltage = voltages.maxOrNull() ?: 0f
    val minVoltage = voltages.minOrNull() ?: 0f
    val avgPower = if (powers.isNotEmpty()) powers.average().toFloat() else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .padding(16.dp)
            .testTag("historical_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Historical Analytics",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "${recentReadings.size} persisted measurements in local database",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = {
                    val csv = buildCsvExport(recentReadings)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("ElectraSense_Telemetry_CSV", csv))
                    Toast.makeText(context, "CSV exported & copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NavyBorder)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("export_csv_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = "Export CSV",
                    tint = ElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export CSV", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Time window selector
        TabRow(
            selectedTabIndex = selectedWindowIndex,
            containerColor = NavyDeep,
            contentColor = ElectricCyan,
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
        ) {
            windowLabels.forEachIndexed { index, label ->
                Tab(
                    selected = selectedWindowIndex == index,
                    onClick = { selectedWindowIndex = index },
                    text = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Statistical KPI Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatBox(
                title = "PEAK CURRENT",
                value = String.format("%.2f A", peakCurrent),
                sub = "Avg: ${String.format("%.2f A", avgCurrent)}",
                color = ElectricCyan,
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = "VOLTAGE RANGE",
                value = "${String.format("%.1f", minVoltage)} - ${String.format("%.1f V", peakVoltage)}",
                sub = "Sag Margin: ${String.format("%.1f V", peakVoltage - minVoltage)}",
                color = GoldElectric,
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = "AVG POWER",
                value = String.format("%.1f W", avgPower),
                sub = "Load Factor: ${if (peakCurrent > 0) String.format("%.0f%%", (avgCurrent / peakCurrent) * 100) else "0%"}",
                color = GreenNormal,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "TELEMETRY LOG RECORDS",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Readings table list
        if (recentReadings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No historical telemetry points stored yet. Stream ESP32 data to populate database.",
                    color = TextTertiary,
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentReadings) { item ->
                    HistoricalRowItem(item)
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = sub, color = TextTertiary, fontSize = 9.sp)
        }
    }
}

@Composable
private fun HistoricalRowItem(item: SensorReading) {
    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(item.timestamp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "#${item.sequenceNumber} • $timeStr",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${String.format("%.1f", item.temperatureC)}°C | ${String.format("%.1f", item.humidityPercent)}% RH | Vib: ${String.format("%.2f", item.vibrationMagnitude)}g",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.2f", item.currentA)} A • ${String.format("%.1f", item.voltageV)} V",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${String.format("%.1f", item.calculatedPower)} W",
                    color = GoldElectric,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

private fun buildCsvExport(readings: List<SensorReading>): String {
    val sb = StringBuilder()
    sb.append("sequence,timestamp_iso,current_a,voltage_v,power_w,temp_c,humidity_pct,vibration_g,health,calibration\n")
    readings.forEach { r ->
        val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date(r.timestamp))
        sb.append("${r.sequenceNumber},$iso,${r.currentA},${r.voltageV},${r.calculatedPower},${r.temperatureC},${r.humidityPercent},${r.vibrationMagnitude},${r.sensorHealth},${r.calibrationStatus}\n")
    }
    return sb.toString()
}
