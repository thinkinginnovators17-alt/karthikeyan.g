package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun TelemetryWaveformChart(
    title: String,
    dataPoints: List<Float>,
    unit: String,
    lineColor: Color = ElectricCyan,
    minY: Float? = null,
    maxY: Float? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NavyCard, RoundedCornerShape(14.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        val calculatedMin = minY ?: ((dataPoints.minOrNull() ?: 0f) * 0.9f).coerceAtLeast(0f)
        val calculatedMax = (maxY ?: ((dataPoints.maxOrNull() ?: 1f) * 1.15f)).coerceAtLeast(calculatedMin + 0.1f)
        val latestValue = dataPoints.lastOrNull() ?: 0f

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%.2f", latestValue),
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        color = lineColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Text(
                text = "MAX: ${String.format("%.1f", calculatedMax)}",
                color = TextTertiary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(NavyDeep, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            if (dataPoints.size < 2) {
                Box(
                    modifier = Modifier.matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Awaiting continuous telemetry points...",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            } else {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw subtle grid lines
                    val gridSteps = 3
                    for (i in 0..gridSteps) {
                        val y = h * (i.toFloat() / gridSteps)
                        drawLine(
                            color = Color(0xFF22304E),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val stepX = w / (dataPoints.size - 1)
                    val valueRange = (calculatedMax - calculatedMin).coerceAtLeast(0.001f)

                    val linePath = Path()
                    val fillPath = Path()

                    dataPoints.forEachIndexed { index, value ->
                        val x = index * stepX
                        val normalized = ((value - calculatedMin) / valueRange).coerceIn(0f, 1f)
                        val y = h - (normalized * h)

                        if (index == 0) {
                            linePath.moveTo(x, y)
                            fillPath.moveTo(x, h)
                            fillPath.lineTo(x, y)
                        } else {
                            linePath.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }
                    }

                    // Close fill path
                    fillPath.lineTo(w, h)
                    fillPath.close()

                    // Draw gradient fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.28f),
                                lineColor.copy(alpha = 0.02f)
                            )
                        )
                    )

                    // Draw stroke line
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )

                    // Draw dot at latest point
                    val lastX = w
                    val lastNorm = ((latestValue - calculatedMin) / valueRange).coerceIn(0f, 1f)
                    val lastY = h - (lastNorm * h)
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(lastX, lastY)
                    )
                    drawCircle(
                        color = lineColor,
                        radius = 2.5.dp.toPx(),
                        center = Offset(lastX, lastY)
                    )
                }
            }
        }
    }
}
