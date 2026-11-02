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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldElectric
import com.example.ui.theme.GreenNormal
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.ElectraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: ElectraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reading by viewModel.currentReading.collectAsState()
    val lineStatus by viewModel.lineStatus.collectAsState()
    val settings by viewModel.deviceSettings.collectAsState()
    val activeAlerts by viewModel.activeAlerts.collectAsState()
    val allAlerts by viewModel.allAlerts.collectAsState()
    val calibrations by viewModel.calibrationRecords.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val dateFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

    val reportText = buildString {
        appendLine("===============================================================")
        appendLine("             ELECTRA SENSE AI — ELECTRICAL AUDIT REPORT        ")
        appendLine("===============================================================")
        appendLine("Report Generated   : $dateFormatted")
        appendLine("Monitoring Station : ${settings.deviceName} (${settings.deviceId})")
        appendLine("Auditor / Operator : $userRole")
        appendLine("Line Overall State : ${lineStatus.displayName}")
        appendLine("Firmware Release   : ${reading?.firmwareVersion ?: "v1.2.4-edu"}")
        appendLine("---------------------------------------------------------------")
        appendLine("1. INSTANTANEOUS TELEMETRY RECORD:")
        if (reading != null) {
            val r = reading!!
            appendLine(" - Current Load    : ${String.format("%.2f", r.currentA)} A (Threshold: ${settings.overcurrentThresholdA} A)")
            appendLine(" - Line Voltage    : ${String.format("%.1f", r.voltageV)} V (Nominal: ${settings.undervoltageThresholdV} - ${settings.overvoltageThresholdV} V)")
            appendLine(" - Calculated Power: ${if (r.isPowerValid) String.format("%.1f", r.calculatedPower) + " W" else "Uncalculated"}")
            appendLine(" - Enclosure Temp  : ${String.format("%.1f", r.temperatureC)} °C (Limit: ${settings.highTempThresholdC} °C)")
            appendLine(" - Relative Hum    : ${String.format("%.1f", r.humidityPercent)} %")
            appendLine(" - Vibration Mag   : ${String.format("%.2f", r.vibrationMagnitude)} g (Limit: ${settings.vibrationThresholdG} g)")
            appendLine(" - Sensor Health   : ${r.sensorHealth}")
            appendLine(" - Calibration     : ${r.calibrationStatus}")
        } else {
            appendLine(" - No active live telemetry streaming at generation timestamp.")
        }
        appendLine("---------------------------------------------------------------")
        appendLine("2. ALERTS & INCIDENT SUMMARY:")
        appendLine(" - Active Unacknowledged Violations : ${activeAlerts.size}")
        appendLine(" - Total Persisted Log Incidents    : ${allAlerts.size}")
        allAlerts.take(5).forEach { a ->
            val aDate = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(a.timestamp))
            appendLine("   * [$aDate] [${a.severity}] ${a.title} (Trigger: ${a.triggeringValue})")
        }
        appendLine("---------------------------------------------------------------")
        appendLine("3. SENSOR CALIBRATION TRACEABILITY:")
        appendLine(" - ACS712 Variant     : ${settings.acs712Variant} (${settings.acs712SensitivityMvPerA} mV/A)")
        appendLine(" - Zero-Current Offset: ${settings.acs712ZeroCurrentVoltageMv} mV")
        appendLine(" - Divider Ratio      : ${settings.voltageDividerRatio}:1")
        if (calibrations.isNotEmpty()) {
            val latestCal = calibrations.first()
            val calDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(latestCal.timestamp))
            appendLine(" - Latest Calibration : $calDate by ${latestCal.technicianName} using ${latestCal.referenceInstrument}")
        }
        appendLine("===============================================================")
        appendLine("EDUCATIONAL NOTICE: This audit report is generated for academic")
        appendLine("and laboratory demonstration. Never touch uninsulated live lines.")
        appendLine("===============================================================")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("reports_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Reports & Export",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Generate and export certified laboratory monitoring summaries",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("ElectraSense_Report", reportText))
                    Toast.makeText(context, "Full report copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("copy_report_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Report",
                    tint = ElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Report", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Formatted Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = GoldElectric,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ELECTRICAL AUDIT PREVIEW",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyDeep, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = reportText,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
