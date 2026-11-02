package com.example.data.ai

import android.util.Log
import com.example.data.model.AlertItem
import com.example.data.model.LineStatus
import com.example.data.model.SensorReading
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

private const val TAG = "ElectraAi"

class ElectraAiAssistant {

    // Lazy initialization per Firebase AI Logic skill
    private val model by lazy {
        try {
            Firebase.ai.generativeModel("gemini-3.8-flash")
        } catch (e: Exception) {
            Log.w(TAG, "Firebase AI model unavailable on current runtime: ${e.message}")
            null
        }
    }

    suspend fun analyzeEvent(
        reading: SensorReading?,
        activeAlerts: List<AlertItem>,
        lineStatus: LineStatus,
        userQuery: String
    ): String = withContext(Dispatchers.IO) {
        val telemetryContext = buildTelemetryContext(reading, activeAlerts, lineStatus)

        val systemPrompt = """
You are the "Electra AI Assistant", an educational electrical engineering diagnostic and safety specialist for the ELECTRA SENSE monitoring prototype.

YOUR OPERATING RULES:
1. Ground your analysis strictly on the provided sensor data. If no valid device data exists, explicitly state: "No live ESP32 telemetry is available yet. I cannot analyze the live electrical system, but I can answer general electrical engineering and safety questions."
2. Clearly distinguish MEASURED FACTS from PREDICTIONS, POSSIBILITIES, or THEORETICAL EXPLANATIONS.
3. NEVER invent measurements or declare a broken wire or short circuit with 100% certainty if evidence is ambiguous. Label uncertain events as "Possible Fault" and identify sensor limitations.
4. ELECTRICAL SAFETY: ALWAYS emphasize non-invasive, safe inspection methods. NEVER instruct users to touch bare conductors, live terminals, or bypass protective breakers. Highlight that this prototype is for low-voltage educational demonstration only.
5. SENSOR LIMITATIONS:
   - ACS712: Hall-effect current sensor, subject to magnetic interference and slight thermal drift (~few mA).
   - Voltage sensor: Resistor divider (e.g. 5:1 ratio), measures DC or rectified signals; resolution limited by ESP32 ADC non-linearity.
   - DHT11: Resistive humidity and NTC thermistor, ±2°C accuracy, 1 Hz max sampling rate.
   - MPU6050: 3-axis MEMS accelerometer, detects physical mechanical oscillation and harmonics.

TELEMETRY CONTEXT:
$telemetryContext

USER QUERY:
$userQuery
""".trimIndent()

        // Attempt Gemini 3.8 Flash model via Firebase AI Logic
        try {
            val genModel = model
            if (genModel != null) {
                val response = withTimeout(45_000L) {
                    genModel.generateContent(systemPrompt)
                }
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return@withContext text.trim()
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.w(TAG, "Gemini call timed out, using onboard diagnostic engine.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Gemini call encountered issue (${e.message}), utilizing onboard engineering rules.")
        }

        // Onboard fallback rule-based electrical reasoning engine (guarantees instantaneous response even if offline)
        return@withContext generateExpertLocalAnalysis(reading, activeAlerts, lineStatus, userQuery)
    }

    private fun buildTelemetryContext(
        reading: SensorReading?,
        activeAlerts: List<AlertItem>,
        lineStatus: LineStatus
    ): String {
        if (reading == null) {
            return "ESP32 STATUS: Disconnected (No live packet received)"
        }
        val sb = StringBuilder()
        sb.append("ESP32 Device: ${reading.deviceId} (Firmware: ${reading.firmwareVersion})\n")
        sb.append("Data Freshness: ${if (reading.isStale) "STALE (Last update: ${reading.timestamp})" else "LIVE & VALID"}\n")
        sb.append("Current: ${String.format("%.2f", reading.currentA)} A\n")
        sb.append("Voltage: ${String.format("%.1f", reading.voltageV)} V\n")
        val powerStr = if (reading.isPowerValid) "${String.format("%.1f", reading.calculatedPower)} W" else "Uncalculated/Insufficient excitation"
        sb.append("Power: $powerStr\n")
        sb.append("Temperature: ${String.format("%.1f", reading.temperatureC)} °C\n")
        sb.append("Humidity: ${String.format("%.1f", reading.humidityPercent)} %\n")
        sb.append("Vibration: ${String.format("%.2f", reading.vibrationMagnitude)} g (X:${reading.vibrationX}, Y:${reading.vibrationY}, Z:${reading.vibrationZ})\n")
        sb.append("Hardware Health: ${reading.sensorHealth}\n")
        sb.append("Line Status: ${lineStatus.displayName} - ${lineStatus.description}\n")
        sb.append("Active Alerts (${activeAlerts.size}):\n")
        activeAlerts.forEach { alert ->
            sb.append(" - [${alert.severity}] ${alert.title}: ${alert.message} (Trigger: ${alert.triggeringValue})\n")
        }
        return sb.toString()
    }

    private fun generateExpertLocalAnalysis(
        r: SensorReading?,
        alerts: List<AlertItem>,
        lineStatus: LineStatus,
        query: String
    ): String {
        if (r == null) {
            return "⚡ **Electra AI Assistant**\n\nNo live ESP32 telemetry is currently connected. I am standing by to analyze electrical data once your DevKit begins streaming.\n\n" +
                    "**Safety Note:** When wiring up prototype circuits on a breadboard, verify all DC power supply polarity before applying voltage. Keep AC mains isolated."
        }

        val q = query.lowercase()
        return when {
            q.contains("voltage") || q.contains("sag") || q.contains("drop") -> {
                "⚡ **Voltage & Line Impedance Analysis**\n\n" +
                "• **Measured Voltage:** ${String.format("%.1f", r.voltageV)} V (Current load: ${String.format("%.2f", r.currentA)} A)\n" +
                "• **Status:** ${if (r.voltageV < 9.0f) "Substantial voltage drop detected under load." else "Voltage is within nominal operating range."}\n\n" +
                "**Possible Explanations:**\n" +
                "1. If voltage drops significantly when current surges, this indicates line resistance or an overloaded DC power source (V = V_source - I × R_line).\n" +
                "2. Loose terminal screws or breadboard contact resistance can cause localized thermal heating and voltage drop.\n\n" +
                "**Safe Verification Steps:**\n" +
                "• De-energize circuit completely before checking terminal screws.\n" +
                "• Check calibration ratio of the voltage divider module (standard 30kΩ/7.5kΩ resistor module has a 5:1 ratio)."
            }
            q.contains("current") || q.contains("overcurrent") || q.contains("load") -> {
                "⚡ **Current & ACS712 Telemetry Breakdown**\n\n" +
                "• **Measured Current:** ${String.format("%.2f", r.currentA)} A\n" +
                "• **Calculated Power:** ${if (r.isPowerValid) String.format("%.1f", r.calculatedPower) + " W" else "N/A"}\n\n" +
                "**Engineering Observations:**\n" +
                "1. The ACS712 Hall-effect sensor outputs VCC/2 (approx 2.5V) at 0A. With the 5A variant, sensitivity is 185 mV/A.\n" +
                "2. If current reading fluctuates while no load is attached, calibrate the zero-current voltage offset in the Hardware & Calibration tab.\n" +
                "3. Continuous loads above 3.5A on prototype PCB traces can cause localized trace warming."
            }
            q.contains("vibration") || q.contains("mpu") || q.contains("motion") -> {
                "⚡ **Vibration & Mechanical Stability Diagnostic**\n\n" +
                "• **Measured Acceleration:** ${String.format("%.2f", r.vibrationMagnitude)} g\n" +
                "• **Vector Breakdown:** X=${r.vibrationX}g, Y=${r.vibrationY}g, Z=${r.vibrationZ}g\n\n" +
                "**Diagnosis:**\n" +
                "1. In stationary baseline conditions, magnitude should equal ~1.00g (due to Earth's gravity on the Z axis when horizontal).\n" +
                "2. Oscillations above 1.5g suggest mechanical vibration from nearby rotating machinery, transformers, or loose conduit fasteners."
            }
            q.contains("summary") || q.contains("report") || q.contains("audit") -> {
                "⚡ **ELECTRA SENSE Monitoring Report**\n\n" +
                "• **Device:** ${r.deviceId} | Firmware: ${r.firmwareVersion}\n" +
                "• **Line Health Status:** ${lineStatus.displayName}\n" +
                "• **Current / Voltage:** ${String.format("%.2f", r.currentA)} A / ${String.format("%.1f", r.voltageV)} V\n" +
                "• **Enclosure Climate:** ${String.format("%.1f", r.temperatureC)} °C | ${String.format("%.1f", r.humidityPercent)}% RH\n" +
                "• **Mechanical Stability:** ${String.format("%.2f", r.vibrationMagnitude)} g (Normal baseline: ~1.00g)\n" +
                "• **Active Alerts Count:** ${alerts.size}\n\n" +
                "**Assessment:** Telemetry reflects ${if (alerts.isEmpty()) "nominal test conditions with no active threshold violations" else "${alerts.size} active warning/fault alert(s) requiring technician review"}."
            }
            else -> {
                "⚡ **Electra AI Diagnostic Summary**\n\n" +
                "• **Live Line Status:** ${lineStatus.displayName} (${lineStatus.description})\n" +
                "• **Telemetry Snapshot:** Current=${String.format("%.2f", r.currentA)}A, Voltage=${String.format("%.1f", r.voltageV)}V, Power=${if (r.isPowerValid) String.format("%.1f", r.calculatedPower) + "W" else "0W"}\n" +
                "• **Environment:** ${String.format("%.1f", r.temperatureC)}°C, ${String.format("%.1f", r.humidityPercent)}% RH, Vibration=${String.format("%.2f", r.vibrationMagnitude)}g\n\n" +
                (if (alerts.isNotEmpty()) "⚠️ **Alert Notice:** ${alerts.first().title}: ${alerts.first().message}\n\n" else "") +
                "**Recommendations:**\n" +
                "• For educational test rigs, keep current loads within safe breadboard limits (<3A).\n" +
                "• Use a calibrated digital multimeter across the test load to verify ACS712 and voltage-divider scaling."
            }
        }
    }
}
