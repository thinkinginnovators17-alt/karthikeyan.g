package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.LineStatus
import com.example.data.model.SensorReading
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ELECTRA SENSE AI", appName)
    }

    @Test
    fun `sensor reading calculates power accurately`() {
        val reading = SensorReading(
            deviceId = "TEST-NODE",
            currentA = 2.0f,
            voltageV = 12.0f,
            powerW = null
        )
        assertTrue(reading.isPowerValid)
        assertEquals(24.0f, reading.calculatedPower, 0.01f)
    }

    @Test
    fun `unexcited line marks power invalid`() {
        val reading = SensorReading(
            deviceId = "TEST-NODE",
            currentA = 0.0f,
            voltageV = 0.1f,
            powerW = null
        )
        assertFalse(reading.isPowerValid)
    }
}
