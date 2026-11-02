package com.example.data.remote

import com.example.data.model.SensorReading
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Esp32ReadingDto(
    @Json(name = "device_id") val deviceId: String,
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "sequence_number") val sequenceNumber: Long,
    @Json(name = "current_a") val currentA: Float,
    @Json(name = "voltage_v") val voltageV: Float,
    @Json(name = "power_w") val powerW: Float? = null,
    @Json(name = "temperature_c") val temperatureC: Float,
    @Json(name = "humidity_percent") val humidityPercent: Float,
    @Json(name = "vibration_x") val vibrationX: Float = 0f,
    @Json(name = "vibration_y") val vibrationY: Float = 0f,
    @Json(name = "vibration_z") val vibrationZ: Float = 0f,
    @Json(name = "vibration_magnitude") val vibrationMagnitude: Float = 0f,
    @Json(name = "sensor_health") val sensorHealth: String = "ALL_OK",
    @Json(name = "calibration_status") val calibrationStatus: String = "CALIBRATED",
    @Json(name = "firmware_version") val firmwareVersion: String = "v1.2.4-edu"
) {
    fun toDomain(): SensorReading {
        return SensorReading(
            deviceId = deviceId,
            sequenceNumber = sequenceNumber,
            timestamp = if (timestamp > 1000000000L) timestamp else System.currentTimeMillis(),
            currentA = currentA,
            voltageV = voltageV,
            powerW = powerW ?: if (voltageV > 0.5f) (voltageV * currentA) else 0f,
            temperatureC = temperatureC,
            humidityPercent = humidityPercent,
            vibrationX = vibrationX,
            vibrationY = vibrationY,
            vibrationZ = vibrationZ,
            vibrationMagnitude = vibrationMagnitude,
            sensorHealth = sensorHealth,
            calibrationStatus = calibrationStatus,
            firmwareVersion = firmwareVersion,
            isStale = false
        )
    }
}

@JsonClass(generateAdapter = true)
data class DeviceHealthResponse(
    val status: String,
    @Json(name = "dht11_ok") val dht11Ok: Boolean,
    @Json(name = "mpu6050_ok") val mpu6050Ok: Boolean,
    @Json(name = "acs712_ready") val acs712Ready: Boolean,
    @Json(name = "voltage_sensor_ready") val voltageSensorReady: Boolean,
    @Json(name = "wifi_rssi") val wifiRssi: Int,
    @Json(name = "free_heap") val freeHeap: Long,
    val uptimeSeconds: Long
)

@JsonClass(generateAdapter = true)
data class CalibrationRequest(
    val sensor: String, // "acs712" or "voltage"
    @Json(name = "zero_offset_mv") val zeroOffsetMv: Float? = null,
    @Json(name = "scale_factor") val scaleFactor: Float? = null
)

@JsonClass(generateAdapter = true)
data class GenericApiResponse(
    val success: Boolean,
    val message: String
)

interface Esp32ApiService {
    @GET("api/readings")
    suspend fun getLatestReading(): Response<Esp32ReadingDto>

    @GET("api/health")
    suspend fun getHealth(): Response<DeviceHealthResponse>

    @POST("api/buzzer/test")
    suspend fun testBuzzer(): Response<GenericApiResponse>

    @POST("api/calibrate")
    suspend fun calibrateSensor(@Body request: CalibrationRequest): Response<GenericApiResponse>
}

object NetworkClientProvider {
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    fun createEsp32Service(ipAddress: String, port: Int): Esp32ApiService {
        val cleanIp = ipAddress.trim().removePrefix("http://").removePrefix("https://").removeSuffix("/")
        val baseUrl = "http://$cleanIp:$port/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(Esp32ApiService::class.java)
    }
}
