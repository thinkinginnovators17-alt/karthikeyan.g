package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldElectric
import com.example.ui.theme.GreenNormal
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ImpactScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("impact_screen")
    ) {
        // Competition Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GreenNormal.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "Quest4Earth 2026",
                    tint = GreenNormal,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Quest4Earth 2026",
                    color = GreenNormal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Sustainability & Impact",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tagline Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "ELECTRA SENSE AI",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Smart Monitoring. Intelligent Insights. Sustainable Energy.",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "An educational and prototype monitoring system designed to promote energy awareness, early fault detection, and responsible maintenance of electrical infrastructure.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "FIVE PILLARS OF SUSTAINABLE OPERATION",
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        ImpactPillarCard(
            title = "1. Electrical Equipment Health",
            icon = Icons.Default.Bolt,
            accent = ElectricCyan,
            description = "Continuous line monitoring of current and thermal levels helps identify overloaded circuits before destructive insulation breakdown or copper fatigue occurs."
        )

        Spacer(modifier = Modifier.height(8.dp))

        ImpactPillarCard(
            title = "2. Real-Time Energy Awareness",
            icon = Icons.Default.Speed,
            accent = GoldElectric,
            description = "By computing active power (W) and tracking current profiles, technicians identify unoptimized standby loads, unbalanced branch circuits, and unnecessary energy consumption."
        )

        Spacer(modifier = Modifier.height(8.dp))

        ImpactPillarCard(
            title = "3. Thermal & Mechanical Harmonics",
            icon = Icons.Default.Lightbulb,
            accent = Color(0xFFFB923C),
            description = "Combining DHT11 temperature sensing with MPU6050 vibration analysis detects loose terminal screws, transformer hum, and motor misalignment before localized fires or cable damage arise."
        )

        Spacer(modifier = Modifier.height(8.dp))

        ImpactPillarCard(
            title = "4. Predictive Maintenance Planning",
            icon = Icons.Default.CheckCircle,
            accent = GreenNormal,
            description = "Planned, targeted servicing based on logged sensor history avoids emergency downtime, reduces scrap cable replacement, and lengthens equipment operational life."
        )

        Spacer(modifier = Modifier.height(8.dp))

        ImpactPillarCard(
            title = "5. Resource Efficiency in Microgrids",
            icon = Icons.Default.Public,
            accent = Color(0xFFA855F7),
            description = "Designed for miniature test lines and educational laboratory microgrids to teach sustainable load balancing and non-invasive circuit verification."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Responsible Science & Evidence Statement
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDeep)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = GoldElectric,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACADEMIC EVIDENCE & INTEGRITY NOTICE",
                        color = GoldElectric,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "As required for competition ethics, ELECTRA SENSE AI demonstrates operational principles and telemetry data without asserting unverified claims of measured energy savings or carbon reductions. All potential benefits describe theoretical engineering mechanisms pending field calibration.",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun ImpactPillarCard(
    title: String,
    icon: ImageVector,
    accent: Color,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}
