package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ElectraAiAssistant
import com.example.data.model.AlertItem
import com.example.data.model.CalibrationRecord
import com.example.data.model.ConnectionStatus
import com.example.data.model.DeviceSettings
import com.example.data.model.ElectraChatMessage
import com.example.data.model.LineStatus
import com.example.data.model.SensorReading
import com.example.data.repository.ElectraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ElectraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ElectraRepository(application)
    private val aiAssistant = ElectraAiAssistant()

    val connectionStatus: StateFlow<ConnectionStatus> = repository.connectionStatus
    val currentReading: StateFlow<SensorReading?> = repository.currentReading
    val lineStatus: StateFlow<LineStatus> = repository.lineStatus
    val wireStatus: StateFlow<com.example.data.model.WireMonitoringStatus> = repository.wireStatus
    val testHistory: StateFlow<List<com.example.data.model.TestRunRecord>> = repository.testHistory
    val deviceSettings: StateFlow<DeviceSettings> = repository.deviceSettings

    val recentReadings: StateFlow<List<SensorReading>> = repository.recentReadings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAlerts: StateFlow<List<AlertItem>> = repository.activeAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAlerts: StateFlow<List<AlertItem>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calibrationRecords: StateFlow<List<CalibrationRecord>> = repository.calibrationRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _chatMessages = MutableStateFlow<List<ElectraChatMessage>>(
        listOf(
            ElectraChatMessage(
                isUser = false,
                content = "👋 Welcome to ELECTRA SENSE AI! I'm your electrical monitoring assistant. Connect your ESP32 DevKit to begin live stream analysis, or ask me any question regarding circuit protection, sensor calibration, or fault diagnostics."
            )
        )
    )
    val chatMessages: StateFlow<List<ElectraChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _aiQuickSummary = MutableStateFlow("Telemetry awaiting ESP32 stream. Connect device to generate real-time diagnostic synthesis.")
    val aiQuickSummary: StateFlow<String> = _aiQuickSummary.asStateFlow()

    private val _userRole = MutableStateFlow("Lead Electrical Technician")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _testMessageFeedback = MutableStateFlow<String?>(null)
    val testMessageFeedback: StateFlow<String?> = _testMessageFeedback.asStateFlow()

    init {
        // Automatically start monitoring on configured IP
        startMonitoring()

        // Periodically refresh AI summary when reading changes
        viewModelScope.launch {
            repository.currentReading.collect { reading ->
                if (reading != null) {
                    val summary = aiAssistant.analyzeEvent(
                        reading = reading,
                        activeAlerts = activeAlerts.value,
                        lineStatus = lineStatus.value,
                        userQuery = "Provide a 2-sentence electrical condition summary for dashboard banner."
                    )
                    _aiQuickSummary.value = summary
                }
            }
        }
    }

    fun startMonitoring() {
        val ip = deviceSettings.value.esp32IpAddress
        val port = deviceSettings.value.esp32Port
        repository.startMonitoring(ip, port)
    }

    fun stopMonitoring() {
        repository.stopMonitoring()
    }

    fun sendUserChatMessage(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = ElectraChatMessage(isUser = true, content = prompt)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isAiThinking.value = true
            try {
                val response = aiAssistant.analyzeEvent(
                    reading = currentReading.value,
                    activeAlerts = activeAlerts.value,
                    lineStatus = lineStatus.value,
                    userQuery = prompt
                )
                val aiMsg = ElectraChatMessage(isUser = false, content = response)
                _chatMessages.value = _chatMessages.value + aiMsg
            } catch (e: Exception) {
                val errorMsg = ElectraChatMessage(
                    isUser = false,
                    content = "Unable to process request: ${e.localizedMessage ?: "Unknown diagnostic failure."}"
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ElectraChatMessage(
                isUser = false,
                content = "Conversation history cleared. How may I assist your electrical testing?"
            )
        )
    }

    fun acknowledgeAlert(id: Long) {
        viewModelScope.launch {
            repository.acknowledgeAlert(id)
        }
    }

    fun acknowledgeAllAlerts() {
        viewModelScope.launch {
            repository.acknowledgeAllAlerts()
        }
    }

    fun triggerHardwareBuzzer() {
        viewModelScope.launch {
            val success = repository.triggerHardwareBuzzer()
            _testMessageFeedback.value = if (success) "Buzzer signal acknowledged by ESP32!" else "ESP32 did not respond to buzzer command."
        }
    }

    fun clearFeedback() {
        _testMessageFeedback.value = null
    }

    fun saveSettings(settings: DeviceSettings) {
        viewModelScope.launch {
            repository.updateSettings(settings)
            startMonitoring()
        }
    }

    fun recordCalibration(record: CalibrationRecord) {
        viewModelScope.launch {
            repository.addCalibrationRecord(record)
            repository.sendHardwareCalibration(
                sensor = if (record.sensorName.contains("ACS", ignoreCase = true)) "acs712" else "voltage",
                zeroOffsetMv = record.zeroOffset,
                scaleFactor = record.scaleFactor
            )
        }
    }

    fun injectLabVerification(
        currentA: Float,
        voltageV: Float,
        tempC: Float = 26.8f,
        humPct: Float = 46.0f,
        vibrationMag: Float = 1.01f
    ) {
        viewModelScope.launch {
            repository.injectEducationalVerificationData(
                currentA = currentA,
                voltageV = voltageV,
                tempC = tempC,
                humPct = humPct,
                vibrationMag = vibrationMag
            )
        }
    }

    fun setUserRole(role: String) {
        _userRole.value = role
    }
}
