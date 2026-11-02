package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDao {
    @Query("SELECT * FROM sensor_readings ORDER BY timestamp DESC LIMIT 1")
    fun getLatestReading(): Flow<SensorEntity?>

    @Query("SELECT * FROM sensor_readings ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentReadings(limit: Int = 100): Flow<List<SensorEntity>>

    @Query("SELECT * FROM sensor_readings WHERE timestamp >= :sinceTimestamp ORDER BY timestamp ASC")
    suspend fun getReadingsSince(sinceTimestamp: Long): List<SensorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: SensorEntity): Long

    @Query("DELETE FROM sensor_readings WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteOldReadings(cutoffTimestamp: Long)

    @Query("SELECT COUNT(*) FROM sensor_readings")
    suspend fun getReadingCount(): Int
}

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE isAcknowledged = 0 ORDER BY timestamp DESC")
    fun getActiveAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity): Long

    @Query("UPDATE alerts SET isAcknowledged = 1, acknowledgedAt = :timestamp WHERE id = :alertId")
    suspend fun acknowledgeAlert(alertId: Long, timestamp: Long)

    @Query("UPDATE alerts SET isAcknowledged = 1, acknowledgedAt = :timestamp WHERE isAcknowledged = 0")
    suspend fun acknowledgeAllAlerts(timestamp: Long)

    @Query("DELETE FROM alerts WHERE id = :alertId")
    suspend fun deleteAlert(alertId: Long)
}

@Dao
interface CalibrationDao {
    @Query("SELECT * FROM calibration_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<CalibrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CalibrationEntity): Long
}

@Dao
interface DeviceSettingsDao {
    @Query("SELECT * FROM device_settings WHERE deviceId = :deviceId LIMIT 1")
    fun getSettings(deviceId: String = "ESP32-ELECTRA-01"): Flow<DeviceSettingsEntity?>

    @Query("SELECT * FROM device_settings WHERE deviceId = :deviceId LIMIT 1")
    suspend fun getSettingsOnce(deviceId: String = "ESP32-ELECTRA-01"): DeviceSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: DeviceSettingsEntity)
}
