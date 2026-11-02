package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AlertItem
import com.example.data.model.AlertSeverity
import com.example.data.model.CalibrationRecord
import com.example.data.model.DeviceSettings
import com.example.data.model.SensorReading

@Entity(tableName = "sensor_readings")
data class SensorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val sequenceNumber: Long,
    val timestamp: Long,
    val currentA: Float,
    val voltageV: Float,
    val powerW: Float?,
    val temperatureC: Float,
    val humidityPercent: Float,
    val vibrationX: Float,
    val vibrationY: Float,
    val vibrationZ: Float,
    val vibrationMagnitude: Float,
    val sensorHealth: String,
    val calibrationStatus: String,
    val firmwareVersion: String
) {
    fun toDomain(isStale: Boolean = false): SensorReading = SensorReading(
        id = id,
        deviceId = deviceId,
        sequenceNumber = sequenceNumber,
        timestamp = timestamp,
        currentA = currentA,
        voltageV = voltageV,
        powerW = powerW,
        temperatureC = temperatureC,
        humidityPercent = humidityPercent,
        vibrationX = vibrationX,
        vibrationY = vibrationY,
        vibrationZ = vibrationZ,
        vibrationMagnitude = vibrationMagnitude,
        sensorHealth = sensorHealth,
        calibrationStatus = calibrationStatus,
        firmwareVersion = firmwareVersion,
        isStale = isStale
    )

    companion object {
        fun fromDomain(d: SensorReading): SensorEntity = SensorEntity(
            id = d.id,
            deviceId = d.deviceId,
            sequenceNumber = d.sequenceNumber,
            timestamp = d.timestamp,
            currentA = d.currentA,
            voltageV = d.voltageV,
            powerW = d.powerW,
            temperatureC = d.temperatureC,
            humidityPercent = d.humidityPercent,
            vibrationX = d.vibrationX,
            vibrationY = d.vibrationY,
            vibrationZ = d.vibrationZ,
            vibrationMagnitude = d.vibrationMagnitude,
            sensorHealth = d.sensorHealth,
            calibrationStatus = d.calibrationStatus,
            firmwareVersion = d.firmwareVersion
        )
    }
}

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val timestamp: Long,
    val ruleId: String,
    val title: String,
    val message: String,
    val severity: String,
    val triggeringValue: String,
    val isAcknowledged: Boolean,
    val acknowledgedAt: Long?,
    val aiExplanation: String?
) {
    fun toDomain(): AlertItem = AlertItem(
        id = id,
        deviceId = deviceId,
        timestamp = timestamp,
        ruleId = ruleId,
        title = title,
        message = message,
        severity = runCatching { AlertSeverity.valueOf(severity) }.getOrDefault(AlertSeverity.WARNING),
        triggeringValue = triggeringValue,
        isAcknowledged = isAcknowledged,
        acknowledgedAt = acknowledgedAt,
        aiExplanation = aiExplanation
    )

    companion object {
        fun fromDomain(a: AlertItem): AlertEntity = AlertEntity(
            id = a.id,
            deviceId = a.deviceId,
            timestamp = a.timestamp,
            ruleId = a.ruleId,
            title = a.title,
            message = a.message,
            severity = a.severity.name,
            triggeringValue = a.triggeringValue,
            isAcknowledged = a.isAcknowledged,
            acknowledgedAt = a.acknowledgedAt,
            aiExplanation = a.aiExplanation
        )
    }
}

@Entity(tableName = "calibration_records")
data class CalibrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val sensorName: String,
    val zeroOffset: Float,
    val scaleFactor: Float,
    val referenceInstrument: String,
    val technicianName: String,
    val notes: String
) {
    fun toDomain(): CalibrationRecord = CalibrationRecord(
        id = id,
        timestamp = timestamp,
        sensorName = sensorName,
        zeroOffset = zeroOffset,
        scaleFactor = scaleFactor,
        referenceInstrument = referenceInstrument,
        technicianName = technicianName,
        notes = notes
    )

    companion object {
        fun fromDomain(c: CalibrationRecord): CalibrationEntity = CalibrationEntity(
            id = c.id,
            timestamp = c.timestamp,
            sensorName = c.sensorName,
            zeroOffset = c.zeroOffset,
            scaleFactor = c.scaleFactor,
            referenceInstrument = c.referenceInstrument,
            technicianName = c.technicianName,
            notes = c.notes
        )
    }
}

@Entity(tableName = "device_settings")
data class DeviceSettingsEntity(
    @PrimaryKey val deviceId: String,
    val deviceName: String,
    val esp32IpAddress: String,
    val esp32Port: Int,
    val pollingIntervalMs: Long,
    val useSupabaseCloud: Boolean,
    val supabaseUrl: String,
    val supabaseApiKey: String,
    val overcurrentThresholdA: Float,
    val overvoltageThresholdV: Float,
    val undervoltageThresholdV: Float,
    val highTempThresholdC: Float,
    val vibrationThresholdG: Float,
    val acs712Variant: String,
    val acs712SensitivityMvPerA: Float,
    val acs712ZeroCurrentVoltageMv: Float,
    val voltageDividerRatio: Float,
    val adcReferenceVoltage: Float
) {
    fun toDomain(): DeviceSettings = DeviceSettings(
        deviceId = deviceId,
        deviceName = deviceName,
        esp32IpAddress = esp32IpAddress,
        esp32Port = esp32Port,
        pollingIntervalMs = pollingIntervalMs,
        useSupabaseCloud = useSupabaseCloud,
        supabaseUrl = supabaseUrl,
        supabaseApiKey = supabaseApiKey,
        overcurrentThresholdA = overcurrentThresholdA,
        overvoltageThresholdV = overvoltageThresholdV,
        undervoltageThresholdV = undervoltageThresholdV,
        highTempThresholdC = highTempThresholdC,
        vibrationThresholdG = vibrationThresholdG,
        acs712Variant = acs712Variant,
        acs712SensitivityMvPerA = acs712SensitivityMvPerA,
        acs712ZeroCurrentVoltageMv = acs712ZeroCurrentVoltageMv,
        voltageDividerRatio = voltageDividerRatio,
        adcReferenceVoltage = adcReferenceVoltage
    )

    companion object {
        fun fromDomain(s: DeviceSettings): DeviceSettingsEntity = DeviceSettingsEntity(
            deviceId = s.deviceId,
            deviceName = s.deviceName,
            esp32IpAddress = s.esp32IpAddress,
            esp32Port = s.esp32Port,
            pollingIntervalMs = s.pollingIntervalMs,
            useSupabaseCloud = s.useSupabaseCloud,
            supabaseUrl = s.supabaseUrl,
            supabaseApiKey = s.supabaseApiKey,
            overcurrentThresholdA = s.overcurrentThresholdA,
            overvoltageThresholdV = s.overvoltageThresholdV,
            undervoltageThresholdV = s.undervoltageThresholdV,
            highTempThresholdC = s.highTempThresholdC,
            vibrationThresholdG = s.vibrationThresholdG,
            acs712Variant = s.acs712Variant,
            acs712SensitivityMvPerA = s.acs712SensitivityMvPerA,
            acs712ZeroCurrentVoltageMv = s.acs712ZeroCurrentVoltageMv,
            voltageDividerRatio = s.voltageDividerRatio,
            adcReferenceVoltage = s.adcReferenceVoltage
        )
    }
}
