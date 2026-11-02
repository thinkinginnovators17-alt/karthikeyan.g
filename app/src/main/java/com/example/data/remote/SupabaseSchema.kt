package com.example.data.remote

object SupabaseSchema {
    val SQL_DDL_MIGRATION = """
-- =====================================================================
-- ELECTRA SENSE AI — Supabase PostgreSQL Schema & Security Policies
-- =====================================================================

-- 1. PROFILES TABLE
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT NOT NULL,
    full_name TEXT,
    role TEXT DEFAULT 'technician' CHECK (role IN ('technician', 'student', 'evaluator', 'admin')),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. DEVICES TABLE
CREATE TABLE IF NOT EXISTS public.devices (
    device_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    model TEXT DEFAULT 'ESP32-DEVKIT-V1',
    ip_address TEXT,
    mac_address TEXT,
    firmware_version TEXT DEFAULT 'v1.2.4-edu',
    status TEXT DEFAULT 'disconnected' CHECK (status IN ('connected', 'disconnected', 'fault')),
    last_seen TIMESTAMPTZ,
    owner_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. SENSOR READINGS TABLE (High-throughput telemetry)
CREATE TABLE IF NOT EXISTS public.sensor_readings (
    id BIGSERIAL PRIMARY KEY,
    device_id TEXT NOT NULL REFERENCES public.devices(device_id) ON DELETE CASCADE,
    sequence_number BIGINT NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    current_a REAL NOT NULL,
    voltage_v REAL NOT NULL,
    power_w REAL,
    temperature_c REAL NOT NULL,
    humidity_percent REAL NOT NULL,
    vibration_x REAL DEFAULT 0.0,
    vibration_y REAL DEFAULT 0.0,
    vibration_z REAL DEFAULT 0.0,
    vibration_magnitude REAL DEFAULT 0.0,
    sensor_health TEXT DEFAULT 'ALL_OK',
    calibration_status TEXT DEFAULT 'CALIBRATED',
    firmware_version TEXT DEFAULT 'v1.2.4-edu'
);
CREATE INDEX IF NOT EXISTS idx_readings_device_time ON public.sensor_readings(device_id, timestamp DESC);

-- 4. ALERTS TABLE
CREATE TABLE IF NOT EXISTS public.alerts (
    id BIGSERIAL PRIMARY KEY,
    device_id TEXT NOT NULL REFERENCES public.devices(device_id) ON DELETE CASCADE,
    rule_id TEXT NOT NULL,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    severity TEXT NOT NULL CHECK (severity IN ('INFO', 'WARNING', 'CRITICAL')),
    triggering_value TEXT,
    timestamp TIMESTAMPTZ DEFAULT NOW(),
    is_acknowledged BOOLEAN DEFAULT FALSE,
    acknowledged_at TIMESTAMPTZ,
    acknowledged_by UUID REFERENCES public.profiles(id),
    ai_explanation TEXT
);
CREATE INDEX IF NOT EXISTS idx_alerts_active ON public.alerts(device_id, is_acknowledged, timestamp DESC);

-- 5. CALIBRATION RECORDS TABLE
CREATE TABLE IF NOT EXISTS public.calibration_records (
    id BIGSERIAL PRIMARY KEY,
    device_id TEXT NOT NULL REFERENCES public.devices(device_id) ON DELETE CASCADE,
    sensor_name TEXT NOT NULL,
    zero_offset REAL NOT NULL,
    scale_factor REAL NOT NULL,
    reference_instrument TEXT,
    technician_name TEXT,
    notes TEXT,
    calibrated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. AI ANALYSIS RECORDS TABLE
CREATE TABLE IF NOT EXISTS public.ai_analysis (
    id BIGSERIAL PRIMARY KEY,
    device_id TEXT NOT NULL REFERENCES public.devices(device_id) ON DELETE CASCADE,
    timestamp TIMESTAMPTZ DEFAULT NOW(),
    prompt TEXT NOT NULL,
    response_text TEXT NOT NULL,
    event_type TEXT,
    confidence_level TEXT
);

-- 7. DEVICE SETTINGS TABLE
CREATE TABLE IF NOT EXISTS public.device_settings (
    device_id TEXT PRIMARY KEY REFERENCES public.devices(device_id) ON DELETE CASCADE,
    overcurrent_threshold_a REAL DEFAULT 3.5,
    overvoltage_threshold_v REAL DEFAULT 24.0,
    undervoltage_threshold_v REAL DEFAULT 8.0,
    high_temp_threshold_c REAL DEFAULT 45.0,
    vibration_threshold_g REAL DEFAULT 1.8,
    acs712_variant TEXT DEFAULT '5A',
    acs712_sensitivity_mv_per_a REAL DEFAULT 185.0,
    acs712_zero_offset_mv REAL DEFAULT 2500.0,
    voltage_divider_ratio REAL DEFAULT 5.0,
    adc_reference_voltage REAL DEFAULT 3.3,
    reporting_interval_ms INT DEFAULT 2000,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- =====================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- =====================================================================
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.devices ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.sensor_readings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.alerts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.calibration_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.ai_analysis ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.device_settings ENABLE ROW LEVEL SECURITY;

-- Allow authenticated users to view profiles & data
CREATE POLICY "Users can view all authenticated sensor data" ON public.sensor_readings
    FOR SELECT TO authenticated USING (true);

-- Allow authenticated users to read alerts and update acknowledgements
CREATE POLICY "Users can view alerts" ON public.alerts
    FOR SELECT TO authenticated USING (true);

CREATE POLICY "Users can acknowledge alerts" ON public.alerts
    FOR UPDATE TO authenticated USING (true) WITH CHECK (true);

-- Devices insert readings using service or authenticated token
CREATE POLICY "Insert readings policy" ON public.sensor_readings
    FOR INSERT TO authenticated WITH CHECK (true);
    """.trimIndent()
}
