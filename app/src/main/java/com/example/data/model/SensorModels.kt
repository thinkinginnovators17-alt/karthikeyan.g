package com.example.data.model

enum class ConnectionStatus {
    CONNECTED,
    CONNECTING,
    DISCONNECTED
}

enum class LineStatus(val displayName: String, val description: String) {
    NORMAL("Normal", "All electrical & physical parameters within safe operational bounds"),
    WARNING("Warning", "One or more parameters nearing threshold limit"),
    POSSIBLE_FAULT("Possible Fault", "Abnormal measurements detected. Requires investigation."),
    CALIBRATION_REQUIRED("Calibration Required", "Sensor zero-offset or scale drift detected. Verification recommended.")
}

enum class AlertSeverity {
    INFO,
    WARNING,
    CRITICAL
}

data class SensorReading(
    val id: Long = 0,
    val deviceId: String = "ESP32-ELECTRA-01",
    val sequenceNumber: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val currentA: Float = 0.0f,
    val voltageV: Float = 0.0f,
    val powerW: Float? = null,
    val temperatureC: Float = 0.0f,
    val humidityPercent: Float = 0.0f,
    val vibrationX: Float = 0.0f,
    val vibrationY: Float = 0.0f,
    val vibrationZ: Float = 0.0f,
    val vibrationMagnitude: Float = 0.0f,
    val sensorHealth: String = "ALL_OK",
    val calibrationStatus: String = "CALIBRATED",
    val firmwareVersion: String = "v1.2.4-edu",
    val isStale: Boolean = false
) {
    val isPowerValid: Boolean
        get() = (powerW != null && powerW > 0f) || (voltageV > 0.5f && currentA > 0f)

    val calculatedPower: Float
        get() = powerW ?: (voltageV * currentA)
}

data class AlertItem(
    val id: Long = 0,
    val deviceId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val ruleId: String,
    val title: String,
    val message: String,
    val severity: AlertSeverity,
    val triggeringValue: String,
    val isAcknowledged: Boolean = false,
    val acknowledgedAt: Long? = null,
    val aiExplanation: String? = null
)

data class DeviceSettings(
    val deviceId: String = "ESP32-ELECTRA-01",
    val deviceName: String = "Lab Line Monitor Alpha",
    val esp32IpAddress: String = "192.168.4.1",
    val esp32Port: Int = 80,
    val pollingIntervalMs: Long = 2000L,
    val useSupabaseCloud: Boolean = false,
    val supabaseUrl: String = "",
    val supabaseApiKey: String = "",
    // Thresholds
    val overcurrentThresholdA: Float = 3.5f,
    val overvoltageThresholdV: Float = 24.0f, // Low-voltage test rig default
    val undervoltageThresholdV: Float = 8.0f,
    val highTempThresholdC: Float = 45.0f,
    val vibrationThresholdG: Float = 1.8f,
    // Sensor Calibration Parameters
    val acs712Variant: String = "5A", // 5A (185 mV/A), 20A (100 mV/A), 30A (66 mV/A)
    val acs712SensitivityMvPerA: Float = 185.0f,
    val acs712ZeroCurrentVoltageMv: Float = 2500.0f,
    val voltageDividerRatio: Float = 5.0f, // Standard 0-25V sensor module ratio (30k/7.5k)
    val adcReferenceVoltage: Float = 3.3f
)

data class CalibrationRecord(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sensorName: String,
    val zeroOffset: Float,
    val scaleFactor: Float,
    val referenceInstrument: String,
    val technicianName: String,
    val notes: String
)

data class ElectraChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val relatedTelemetry: String? = null
)

enum class WireMonitoringStatus(val displayName: String, val description: String) {
    NORMAL("NORMAL", "Conductor path continuous, impedance nominal within acceptable tolerances"),
    BREAK_SUSPECTED("BREAK SUSPECTED", "Voltage present without current flow or sudden drop in continuity; inspect terminal connections"),
    FAULT_DETECTED("FAULT DETECTED", "Abnormal line impedance or ground leakage detected under active excitation"),
    SENSOR_UNAVAILABLE("SENSOR UNAVAILABLE", "Current/voltage sensing transducers disconnected or uncalibrated")
}

data class TestRunRecord(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val testDescription: String,
    val observedResult: String,
    val calibrationStatus: String,
    val isSimulated: Boolean,
    val testNotes: String,
    val currentA: Float,
    val voltageV: Float,
    val powerW: Float
)
