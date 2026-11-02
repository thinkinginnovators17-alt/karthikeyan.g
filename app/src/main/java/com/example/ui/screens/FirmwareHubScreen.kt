package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firmware.Esp32FirmwareTemplate
import com.example.data.model.CalibrationRecord
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldElectric
import com.example.ui.theme.GreenNormal
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.OrangeWarning
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.ElectraViewModel

@Composable
fun FirmwareHubScreen(
    viewModel: ElectraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.deviceSettings.collectAsState()
    val feedbackMessage by viewModel.testMessageFeedback.collectAsState()
    val calibrationRecords by viewModel.calibrationRecords.collectAsState()

    var showFirmwareCode by remember { mutableStateOf(false) }
    var showWiringGuide by remember { mutableStateOf(true) }
    var showCalibrationDialog by remember { mutableStateOf(false) }

    // Calibration form states
    var zeroOffsetInput by remember(settings.acs712ZeroCurrentVoltageMv) {
        mutableStateOf(settings.acs712ZeroCurrentVoltageMv.toString())
    }
    var voltageRatioInput by remember(settings.voltageDividerRatio) {
        mutableStateOf(settings.voltageDividerRatio.toString())
    }
    var refInstrumentInput by remember { mutableStateOf("Fluke 87V DMM") }
    var technicianInput by remember { mutableStateOf("Lab Technician A") }
    var notesInput by remember { mutableStateOf("Baseline zero-current offset calibrated with open circuit.") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("firmware_hub_screen")
    ) {
        Text(
            text = "Hardware & Calibration Hub",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "ESP32 DevKit integration, pinout map, buzzer test, and sensor calibration",
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Hardware test actions card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "ESP32 DIRECT HARDWARE ACTIONS",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Target Device: http://${settings.esp32IpAddress}:${settings.esp32Port}",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.triggerHardwareBuzzer() },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GoldElectric)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("test_buzzer_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Test Buzzer",
                            tint = GoldElectric,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Trigger Buzzer (GPIO18)", color = GoldElectric, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (feedbackMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = feedbackMessage!!,
                        color = GreenNormal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Wiring pinout card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showWiringGuide = !showWiringGuide },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Cable, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ESP32 DevKit Pinout Reference", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(
                        imageVector = if (showWiringGuide) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                AnimatedVisibility(visible = showWiringGuide) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        PinoutRow("ACS712 Current Sensor", "GPIO 34 (ADC1_CH6)", "Analog OUT")
                        PinoutRow("Voltage Sensor Module", "GPIO 35 (ADC1_CH7)", "Analog OUT (S)")
                        PinoutRow("DHT11 Temperature/Hum", "GPIO 4", "Data (4.7k Pull-up)")
                        PinoutRow("MPU6050 Accelerometer", "GPIO 21 (SDA) / 22 (SCL)", "I2C Bus")
                        PinoutRow("Passive Buzzer", "GPIO 18", "PWM Alert Tone")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sensor Calibration Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SENSOR CALIBRATION WIZARD",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Calibrate zero-current voltage and divider scale against laboratory multimeter standards.",
                    color = TextTertiary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = zeroOffsetInput,
                        onValueChange = { zeroOffsetInput = it },
                        label = { Text("ACS712 Zero mV", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                    OutlinedTextField(
                        value = voltageRatioInput,
                        onValueChange = { voltageRatioInput = it },
                        label = { Text("Voltage Ratio", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = refInstrumentInput,
                    onValueChange = { refInstrumentInput = it },
                    label = { Text("Reference Instrument", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NavyBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Calibration Notes", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NavyBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val zeroMv = zeroOffsetInput.toFloatOrNull() ?: 2500f
                        val ratio = voltageRatioInput.toFloatOrNull() ?: 5.0f
                        val updated = settings.copy(
                            acs712ZeroCurrentVoltageMv = zeroMv,
                            voltageDividerRatio = ratio
                        )
                        viewModel.saveSettings(updated)
                        viewModel.recordCalibration(
                            CalibrationRecord(
                                sensorName = "ACS712 ${settings.acs712Variant}",
                                zeroOffset = zeroMv,
                                scaleFactor = ratio,
                                referenceInstrument = refInstrumentInput,
                                technicianName = technicianInput,
                                notes = notesInput
                            )
                        )
                        Toast.makeText(context, "Calibration saved to database & dispatched!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_calibration_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = NavyDarkest, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save & Dispatch Calibration", color = NavyDarkest, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Arduino C++ Firmware Code Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showFirmwareCode = !showFirmwareCode },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = GoldElectric, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("ESP32 Arduino Firmware Code", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("electra_sense_esp32.ino (v1.2.4-edu)", color = TextTertiary, fontSize = 10.sp)
                        }
                    }
                    Icon(
                        imageVector = if (showFirmwareCode) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (showFirmwareCode) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ESP32_Firmware", Esp32FirmwareTemplate.ARDUINO_CPP_CODE))
                            Toast.makeText(context, "Full Arduino C++ code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("copy_firmware_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Full C++ Code to Clipboard", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyDeep, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = Esp32FirmwareTemplate.ARDUINO_CPP_CODE.take(800) + "\n\n... [Tap 'Copy Full C++ Code' above for full 250+ lines] ...",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun PinoutRow(module: String, pin: String, signal: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = module, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = signal, color = TextTertiary, fontSize = 9.sp)
        }
        Text(
            text = pin,
            color = ElectricCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
