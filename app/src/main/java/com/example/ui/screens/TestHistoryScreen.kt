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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.TestRunRecord
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TestHistoryScreen(
    viewModel: ElectraViewModel,
    modifier: Modifier = Modifier
) {
    val testList by viewModel.testHistory.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, HARDWARE, SIMULATOR

    val filtered = when (selectedFilter) {
        "HARDWARE" -> testList.filter { !it.isSimulated }
        "SIMULATOR" -> testList.filter { it.isSimulated }
        else -> testList
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .padding(16.dp)
            .testTag("test_history_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Test History & Verification",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Competition evaluation logs with hardware vs simulator distinction",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChipItem(
                label = "All Tests (${testList.size})",
                selected = selectedFilter == "ALL",
                onClick = { selectedFilter = "ALL" }
            )
            FilterChipItem(
                label = "Hardware Runs (${testList.count { !it.isSimulated }})",
                selected = selectedFilter == "HARDWARE",
                onClick = { selectedFilter = "HARDWARE" }
            )
            FilterChipItem(
                label = "Simulations (${testList.count { it.isSimulated }})",
                selected = selectedFilter == "SIMULATOR",
                onClick = { selectedFilter = "SIMULATOR" }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Competition Scenario Quick Runner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "EVALUATOR TEST INJECTION SUITE",
                    color = GoldElectric,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Execute test runs to simulate specific fault states safely during evaluation:",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.injectLabVerification(
                                currentA = 1.2f,
                                voltageV = 12.0f,
                                tempC = 27.0f,
                                humPct = 48.0f,
                                vibrationMag = 1.01f
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenNormal.copy(alpha = 0.2f)),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(GreenNormal))
                    ) {
                        Text("1. Nominal", color = GreenNormal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.injectLabVerification(
                                currentA = 4.8f,
                                voltageV = 11.1f,
                                tempC = 42.0f,
                                humPct = 55.0f,
                                vibrationMag = 1.25f
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = RedFault.copy(alpha = 0.2f)),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(RedFault))
                    ) {
                        Text("2. Overcurrent", color = RedFault, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.injectLabVerification(
                                currentA = 0.0f,
                                voltageV = 12.0f,
                                tempC = 26.5f,
                                humPct = 46.0f,
                                vibrationMag = 1.0f
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeWarning.copy(alpha = 0.2f)),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(OrangeWarning))
                    ) {
                        Text("3. Wire Break", color = OrangeWarning, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // History items
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { record ->
                TestRecordCard(record)
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) ElectricCyan.copy(alpha = 0.2f) else NavyCard)
            .border(
                1.dp,
                if (selected) ElectricCyan else NavyBorder,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        OutlinedButton(
            onClick = onClick,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            modifier = Modifier.height(24.dp),
            border = null
        ) {
            Text(
                text = label,
                color = if (selected) ElectricCyan else TextSecondary,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun TestRecordCard(record: TestRunRecord) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(record.timestamp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (record.isSimulated) GoldElectric.copy(alpha = 0.2f) else GreenNormal.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (record.isSimulated) "DEMO SIMULATOR" else "HARDWARE VERIFIED",
                            color = if (record.isSimulated) GoldElectric else GreenNormal,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateStr,
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "Cal: ${record.calibrationStatus}",
                    color = TextSecondary,
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = record.testDescription,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Observed Result: ${record.observedResult}",
                color = ElectricCyan,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Test Notes: ${record.testNotes}",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyDeep, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Current: ${String.format("%.2f", record.currentA)} A", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text(text = "Voltage: ${String.format("%.1f", record.voltageV)} V", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text(text = "Power: ${String.format("%.1f", record.powerW)} W", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}
