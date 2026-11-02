package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldElectric
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SafetyNoticeBanner(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GoldElectric.copy(alpha = 0.08f))
            .border(1.dp, GoldElectric.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(12.dp)
            .testTag("safety_notice_banner")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Safety",
                tint = GoldElectric,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "EDUCATIONAL PROTOTYPE SYSTEM",
                color = GoldElectric,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle",
                tint = GoldElectric,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "This system is an educational monitoring aid and is not a certified electrical protection breaker. Never touch exposed conductors or live AC mains.",
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                SafetyRuleItem("1. Low Voltage Demonstration", "Always test with bench DC power supplies (<24V DC) before any high-energy evaluation.")
                SafetyRuleItem("2. De-energize First", "Disconnect all power sources completely before modifying ACS712 or voltage sensor wiring.")
                SafetyRuleItem("3. Breadboard Current Limits", "Standard solderless breadboards cannot safely carry >2A continuous. Solder or use screw terminals for higher currents.")
                SafetyRuleItem("4. Ground Loop Isolation", "Ensure common ground reference between ESP32 and sensor modules without causing earth ground loops.")
            }
        }
    }
}

@Composable
private fun SafetyRuleItem(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(text = title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = desc, color = TextSecondary, fontSize = 10.sp, lineHeight = 14.sp)
    }
}
