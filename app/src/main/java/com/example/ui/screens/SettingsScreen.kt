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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.SupabaseSchema
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldElectric
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.ElectraViewModel

@Composable
fun SettingsScreen(
    viewModel: ElectraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.deviceSettings.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    var ipInput by remember(settings.esp32IpAddress) { mutableStateOf(settings.esp32IpAddress) }
    var portInput by remember(settings.esp32Port) { mutableStateOf(settings.esp32Port.toString()) }
    var intervalInput by remember(settings.pollingIntervalMs) { mutableStateOf(settings.pollingIntervalMs.toString()) }

    var overcurrentInput by remember(settings.overcurrentThresholdA) { mutableStateOf(settings.overcurrentThresholdA.toString()) }
    var overvoltageInput by remember(settings.overvoltageThresholdV) { mutableStateOf(settings.overvoltageThresholdV.toString()) }
    var undervoltageInput by remember(settings.undervoltageThresholdV) { mutableStateOf(settings.undervoltageThresholdV.toString()) }
    var tempThresholdInput by remember(settings.highTempThresholdC) { mutableStateOf(settings.highTempThresholdC.toString()) }
    var vibrationThresholdInput by remember(settings.vibrationThresholdG) { mutableStateOf(settings.vibrationThresholdG.toString()) }

    var useSupabase by remember(settings.useSupabaseCloud) { mutableStateOf(settings.useSupabaseCloud) }
    var supabaseUrlInput by remember(settings.supabaseUrl) { mutableStateOf(settings.supabaseUrl) }
    var supabaseKeyInput by remember(settings.supabaseApiKey) { mutableStateOf(settings.supabaseApiKey) }

    var showSchemaSql by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = "Settings & Configuration",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "ESP32 network endpoints, alert triggers, cloud backend, and Play release metadata",
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ESP32 Network Settings Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ESP32 Network Endpoint", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = ipInput,
                    onValueChange = { ipInput = it },
                    label = { Text("ESP32 IP Address / Hostname") },
                    placeholder = { Text("192.168.4.1") },
                    modifier = Modifier.fillMaxWidth().testTag("esp32_ip_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NavyBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = portInput,
                        onValueChange = { portInput = it },
                        label = { Text("HTTP Port") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                    OutlinedTextField(
                        value = intervalInput,
                        onValueChange = { intervalInput = it },
                        label = { Text("Poll Interval (ms)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Alert Thresholds Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = GoldElectric, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Safety & Anomaly Thresholds", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = overcurrentInput,
                        onValueChange = { overcurrentInput = it },
                        label = { Text("Overcurrent (A)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                    OutlinedTextField(
                        value = overvoltageInput,
                        onValueChange = { overvoltageInput = it },
                        label = { Text("Overvoltage (V)") },
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = undervoltageInput,
                        onValueChange = { undervoltageInput = it },
                        label = { Text("Undervoltage (V)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                    OutlinedTextField(
                        value = tempThresholdInput,
                        onValueChange = { tempThresholdInput = it },
                        label = { Text("High Temp (°C)") },
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
                    value = vibrationThresholdInput,
                    onValueChange = { vibrationThresholdInput = it },
                    label = { Text("Vibration Magnitude Limit (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NavyBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Supabase PostgreSQL Cloud Sync Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Cloud, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Supabase Cloud Sync", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Switch(
                        checked = useSupabase,
                        onCheckedChange = { useSupabase = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElectricCyan,
                            checkedTrackColor = NavyDeep
                        )
                    )
                }

                AnimatedVisibility(visible = useSupabase) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        OutlinedTextField(
                            value = supabaseUrlInput,
                            onValueChange = { supabaseUrlInput = it },
                            label = { Text("Supabase URL") },
                            placeholder = { Text("https://your-ref.supabase.co") },
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
                            value = supabaseKeyInput,
                            onValueChange = { supabaseKeyInput = it },
                            label = { Text("Anon API Key") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = NavyBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showSchemaSql) "Hide Supabase SQL Schema" else "View Supabase SQL Schema",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showSchemaSql = !showSchemaSql }
                    )

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Supabase_SQL", SupabaseSchema.SQL_DDL_MIGRATION))
                            Toast.makeText(context, "Full Supabase SQL schema copied!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy SQL", color = ElectricCyan, fontSize = 10.sp)
                    }
                }

                if (showSchemaSql) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyDeep, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = SupabaseSchema.SQL_DDL_MIGRATION.take(600) + "\n\n... [Tap 'Copy SQL' above to copy full migration] ...",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Save Button
        Button(
            onClick = {
                val updated = settings.copy(
                    esp32IpAddress = ipInput.trim(),
                    esp32Port = portInput.toIntOrNull() ?: 80,
                    pollingIntervalMs = intervalInput.toLongOrNull() ?: 2000L,
                    overcurrentThresholdA = overcurrentInput.toFloatOrNull() ?: 3.5f,
                    overvoltageThresholdV = overvoltageInput.toFloatOrNull() ?: 24.0f,
                    undervoltageThresholdV = undervoltageInput.toFloatOrNull() ?: 8.0f,
                    highTempThresholdC = tempThresholdInput.toFloatOrNull() ?: 45.0f,
                    vibrationThresholdG = vibrationThresholdInput.toFloatOrNull() ?: 1.8f,
                    useSupabaseCloud = useSupabase,
                    supabaseUrl = supabaseUrlInput.trim(),
                    supabaseApiKey = supabaseKeyInput.trim()
                )
                viewModel.saveSettings(updated)
                Toast.makeText(context, "Settings saved to local storage!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().testTag("save_settings_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = NavyDarkest, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save All Settings", color = NavyDarkest, fontSize = 13.sp, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Google Play Release & About Details
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google Play Release & System Identity", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                AboutRow("App Name", "ELECTRA SENSE AI")
                AboutRow("Package ID", "com.aistudio.electrasense.esv3")
                AboutRow("Version", "1.0 (Build 1) • Target SDK 36")
                AboutRow("License", "Educational & Academic Research")
                AboutRow("Privacy Policy", "Zero third-party telemetry harvesting")
                AboutRow("Developer Contact", "thinkinginnovators17@gmail.com")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Text(text = value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
