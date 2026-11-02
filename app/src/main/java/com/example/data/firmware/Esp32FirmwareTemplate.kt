package com.example.data.firmware

object Esp32FirmwareTemplate {
    val ARDUINO_CPP_CODE = """
/*
 * =====================================================================
 * ELECTRA SENSE AI — ESP32 Sensor Hub Firmware (Arduino Framework)
 * Version: 1.2.4-edu
 * Target: ESP32 DevKit V1
 * =====================================================================
 * HARDWARE CONNECTIONS:
 * - ACS712 Current Sensor Analog OUT  --> GPIO 34 (ADC1_CH6)
 * - Voltage Sensor Module OUT         --> GPIO 35 (ADC1_CH7)
 * - DHT11 Data Pin                    --> GPIO 4  (Pull-up 4.7k to 3.3V)
 * - MPU6050 Accelerometer/Gyro SDA    --> GPIO 21 (I2C SDA)
 * - MPU6050 Accelerometer/Gyro SCL    --> GPIO 22 (I2C SCL)
 * - Passive Buzzer Positive           --> GPIO 18 (PWM Tone / Alert)
 * 
 * REQUIRED ARDUINO LIBRARIES:
 * 1. "DHT sensor library" by Adafruit
 * 2. "Adafruit MPU6050" + "Adafruit Unified Sensor"
 * 3. "ArduinoJson" by Benoit Blanchon (v6 or v7)
 * 4. "WiFi" & "WebServer" (built-in to ESP32 core)
 * =====================================================================
 */

#include <WiFi.h>
#include <WebServer.h>
#include <Wire.h>
#include <DHT.h>
#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>
#include <ArduinoJson.h>

// ----------------- CONFIGURATION -----------------
const char* WIFI_SSID     = "ELECTRA_LAB_WIFI";
const char* WIFI_PASSWORD = "YourSecurePassword";

const char* DEVICE_ID       = "ESP32-ELECTRA-01";
const char* FIRMWARE_VER    = "v1.2.4-edu";

// Pin Definitions
#define PIN_ACS712         34
#define PIN_VOLTAGE_SENSOR 35
#define PIN_DHT11          4
#define PIN_BUZZER         18
#define DHTTYPE            DHT11

// Calibration & Sensor Constants
// ACS712: 5A variant = 185 mV/A, 20A = 100 mV/A, 30A = 66 mV/A
float acsSensitivityMvPerA = 185.0; // 5A model
float acsZeroOffsetMv       = 2500.0; // Nominal 2.5V zero point (VCC/2)
float voltageDividerRatio   = 5.0;    // Standard 0-25V sensor module (5:1)
const float ADC_REF_MV      = 3300.0;
const int ADC_RESOLUTION    = 4095;   // 12-bit ADC

// Peripherals
DHT dht(PIN_DHT11, DHTTYPE);
Adafruit_MPU6050 mpu;
WebServer server(80);

// Telemetry State
unsigned long sequenceNumber = 0;
bool dhtOk = false;
bool mpuOk = false;

// ----------------- HELPER FUNCTIONS -----------------
float readAcs712Current() {
  const int SAMPLES = 40;
  long sumMv = 0;
  for (int i = 0; i < SAMPLES; i++) {
    int raw = analogRead(PIN_ACS712);
    sumMv += (raw * ADC_REF_MV) / ADC_RESOLUTION;
    delayMicroseconds(250);
  }
  float avgMv = (float)sumMv / SAMPLES;
  float voltageDiff = avgMv - acsZeroOffsetMv;
  float currentAmps = voltageDiff / acsSensitivityMvPerA;
  if (abs(currentAmps) < 0.04) currentAmps = 0.0; // Noise floor deadband
  return abs(currentAmps);
}

float readLineVoltage() {
  const int SAMPLES = 30;
  long sumMv = 0;
  for (int i = 0; i < SAMPLES; i++) {
    int raw = analogRead(PIN_VOLTAGE_SENSOR);
    sumMv += (raw * ADC_REF_MV) / ADC_RESOLUTION;
    delayMicroseconds(200);
  }
  float avgMv = (float)sumMv / SAMPLES;
  float pinVolts = avgMv / 1000.0;
  float measuredLineVolts = pinVolts * voltageDividerRatio;
  if (measuredLineVolts < 0.3) measuredLineVolts = 0.0;
  return measuredLineVolts;
}

void triggerBuzzerAlert(int frequency = 2200, int durationMs = 150) {
  tone(PIN_BUZZER, frequency, durationMs);
}

// ----------------- HTTP HANDLERS -----------------
void handleGetReadings() {
  sequenceNumber++;
  
  float currentA = readAcs712Current();
  float voltageV = readLineVoltage();
  float powerW   = (voltageV > 0.5 && currentA > 0.0) ? (voltageV * currentA) : 0.0;
  
  float tempC = dhtOk ? dht.readTemperature() : 0.0;
  float humPct = dhtOk ? dht.readHumidity() : 0.0;
  if (isnan(tempC)) tempC = 0.0;
  if (isnan(humPct)) humPct = 0.0;

  float ax = 0.0, ay = 0.0, az = 0.0, mag = 0.0;
  if (mpuOk) {
    sensors_event_t a, g, temp;
    if (mpu.getEvent(&a, &g, &temp)) {
      ax = a.acceleration.x / 9.80665; // Convert m/s^2 to g
      ay = a.acceleration.y / 9.80665;
      az = a.acceleration.z / 9.80665;
      mag = sqrt(ax * ax + ay * ay + az * az);
    }
  }

  StaticJsonDocument<512> doc;
  doc["device_id"]            = DEVICE_ID;
  doc["timestamp"]            = millis();
  doc["sequence_number"]     = sequenceNumber;
  doc["current_a"]            = round(currentA * 100.0) / 100.0;
  doc["voltage_v"]            = round(voltageV * 10.0) / 10.0;
  doc["power_w"]              = round(powerW * 10.0) / 10.0;
  doc["temperature_c"]        = round(tempC * 10.0) / 10.0;
  doc["humidity_percent"]     = round(humPct * 10.0) / 10.0;
  doc["vibration_x"]          = round(ax * 100.0) / 100.0;
  doc["vibration_y"]          = round(ay * 100.0) / 100.0;
  doc["vibration_z"]          = round(az * 100.0) / 100.0;
  doc["vibration_magnitude"]  = round(mag * 100.0) / 100.0;
  doc["sensor_health"]        = (dhtOk && mpuOk) ? "ALL_OK" : (!dhtOk ? "DHT_ERR" : "MPU_ERR");
  doc["calibration_status"]   = "CALIBRATED";
  doc["firmware_version"]     = FIRMWARE_VER;

  String jsonStr;
  serializeJson(doc, jsonStr);
  server.sendHeader("Access-Control-Allow-Origin", "*");
  server.send(200, "application/json", jsonStr);
}

void handleGetHealth() {
  StaticJsonDocument<384> doc;
  doc["status"]               = "online";
  doc["dht11_ok"]             = dhtOk;
  doc["mpu6050_ok"]           = mpuOk;
  doc["acs712_ready"]         = true;
  doc["voltage_sensor_ready"] = true;
  doc["wifi_rssi"]            = WiFi.RSSI();
  doc["free_heap"]            = ESP.getFreeHeap();
  doc["uptime_seconds"]       = millis() / 1000;

  String jsonStr;
  serializeJson(doc, jsonStr);
  server.sendHeader("Access-Control-Allow-Origin", "*");
  server.send(200, "application/json", jsonStr);
}

void handleTestBuzzer() {
  triggerBuzzerAlert(2600, 250);
  server.sendHeader("Access-Control-Allow-Origin", "*");
  server.send(200, "application/json", "{\"success\":true,\"message\":\"Buzzer triggered\"}");
}

void handleCalibrate() {
  if (server.hasArg("plain")) {
    StaticJsonDocument<256> req;
    deserializeJson(req, server.arg("plain"));
    const char* sensor = req["sensor"];
    if (sensor && strcmp(sensor, "acs712") == 0) {
      if (req.containsKey("zero_offset_mv")) {
        acsZeroOffsetMv = req["zero_offset_mv"];
      }
    }
  }
  server.sendHeader("Access-Control-Allow-Origin", "*");
  server.send(200, "application/json", "{\"success\":true,\"message\":\"Calibration updated\"}");
}

// ----------------- SETUP & LOOP -----------------
void setup() {
  Serial.begin(115200);
  pinMode(PIN_BUZZER, OUTPUT);
  pinMode(PIN_ACS712, INPUT);
  pinMode(PIN_VOLTAGE_SENSOR, INPUT);
  analogReadResolution(12);

  // Initialize DHT11
  dht.begin();
  float testT = dht.readTemperature();
  dhtOk = !isnan(testT);

  // Initialize MPU6050
  Wire.begin(21, 22);
  if (mpu.begin()) {
    mpuOk = true;
    mpu.setAccelerometerRange(MPU6050_RANGE_4_G);
    mpu.setFilterBandwidth(MPU6050_BAND_21_HZ);
  } else {
    mpuOk = false;
    Serial.println("Warning: MPU6050 initialization failed");
  }

  // Connect to Wi-Fi
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 20) {
    delay(500);
    Serial.print(".");
    attempts++;
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.print("\nWiFi Connected! IP Address: ");
    Serial.println(WiFi.localIP());
    triggerBuzzerAlert(1800, 100);
  } else {
    // Fallback: Start SoftAP if Lab Wi-Fi not reachable
    WiFi.softAP("ELECTRA-SENSE-AP", "12345678");
    Serial.println("\nStarted SoftAP: 192.168.4.1");
  }

  // Configure REST Endpoints
  server.on("/api/readings", HTTP_GET, handleGetReadings);
  server.on("/api/health", HTTP_GET, handleGetHealth);
  server.on("/api/buzzer/test", HTTP_POST, handleTestBuzzer);
  server.on("/api/calibrate", HTTP_POST, handleCalibrate);
  server.begin();
  Serial.println("ELECTRA SENSE HTTP Server started.");
}

void loop() {
  server.handleClient();
  delay(2);
}
""".trimIndent()

    val WIRING_GUIDE = """
ESP32 DevKit V1 Pin Connections:
-------------------------------------------------------
1. ACS712 Current Sensor:
   - VCC  --> 5V (or VIN)
   - GND  --> GND
   - OUT  --> GPIO 34 (ADC1 Channel 6)

2. Voltage Sensor Module:
   - VCC (+)  --> Source test line positive (max 25V DC)
   - GND (-)  --> Source test line ground
   - OUT (S)  --> GPIO 35 (ADC1 Channel 7)
   - Minus    --> ESP32 GND (Common reference)

3. DHT11 Temp/Humidity:
   - VCC  --> 3.3V
   - GND  --> GND
   - DATA --> GPIO 4 (with 4.7k pull-up resistor to 3.3V)

4. MPU6050 Accelerometer/Gyro:
   - VCC  --> 3.3V
   - GND  --> GND
   - SDA  --> GPIO 21
   - SCL  --> GPIO 22

5. Passive Buzzer:
   - Positive (+) --> GPIO 18
   - Negative (-) --> GND
-------------------------------------------------------
SAFETY NOTICE: Educational low-voltage demonstration setups only.
Do NOT connect line-level AC mains (>50V) directly to prototype modules.
""".trimIndent()
}
