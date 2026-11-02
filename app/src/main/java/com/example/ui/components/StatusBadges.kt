package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.data.model.LineStatus
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldElectric
import com.example.ui.theme.GreenNormal
import com.example.ui.theme.OrangeWarning
import com.example.ui.theme.RedFault
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConnectionBadge(
    status: ConnectionStatus,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val (bgColor, dotColor, text) = when (status) {
        ConnectionStatus.CONNECTED -> Triple(
            ElectricCyan.copy(alpha = 0.15f),
            ElectricCyan,
            "ESP32 Connected"
        )
        ConnectionStatus.CONNECTING -> Triple(
            GoldElectric.copy(alpha = 0.15f),
            GoldElectric,
            "Connecting..."
        )
        ConnectionStatus.DISCONNECTED -> Triple(
            Color(0xFF334155).copy(alpha = 0.4f),
            Color(0xFF94A3B8),
            "ESP32 Disconnected"
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, dotColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("connection_badge")
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .alpha(if (status == ConnectionStatus.CONNECTING || status == ConnectionStatus.CONNECTED) alphaAnim else 1f)
                .background(dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = if (status == ConnectionStatus.DISCONNECTED) TextSecondary else TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LineStatusBadge(
    status: LineStatus,
    modifier: Modifier = Modifier
) {
    val (badgeColor, icon, label) = when (status) {
        LineStatus.NORMAL -> Triple(GreenNormal, Icons.Default.CheckCircle, "Normal Line")
        LineStatus.WARNING -> Triple(OrangeWarning, Icons.Default.Warning, "Warning Level")
        LineStatus.POSSIBLE_FAULT -> Triple(RedFault, Icons.Default.Warning, "Possible Fault")
        LineStatus.CALIBRATION_REQUIRED -> Triple(GoldElectric, Icons.Default.Info, "Calibrating Needed")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(badgeColor.copy(alpha = 0.18f))
            .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("line_status_badge")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = badgeColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = badgeColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FreshnessBadge(
    timestamp: Long,
    isStale: Boolean,
    modifier: Modifier = Modifier
) {
    val timeDiffSeconds = (System.currentTimeMillis() - timestamp) / 1000
    val timeFormatted = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

    val (color, text) = when {
        isStale -> Pair(RedFault, "STALE • $timeFormatted (${timeDiffSeconds}s ago)")
        timeDiffSeconds <= 3 -> Pair(GreenNormal, "LIVE • $timeFormatted")
        timeDiffSeconds <= 8 -> Pair(GoldElectric, "RECENT • $timeFormatted")
        else -> Pair(OrangeWarning, "DELAYED • $timeFormatted")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
            .testTag("freshness_badge")
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
