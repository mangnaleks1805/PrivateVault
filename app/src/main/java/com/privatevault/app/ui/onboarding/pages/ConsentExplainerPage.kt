package com.privatevault.app.ui.onboarding.pages

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatevault.app.ui.theme.*

@Composable
fun ConsentExplainerPage(
    isAcknowledged: Boolean,
    onAcknowledgeToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text(
            text = "Consent & Respect",
            style = MaterialTheme.typography.headlineLarge,
            color = TitaniumWhite,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Recording requires explicit knowledge and consent from everyone involved.",
            style = MaterialTheme.typography.bodyLarge,
            color = TitaniumWhite
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TitaniumSurfaceGlass)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Core Safety Principles",
                    style = MaterialTheme.typography.titleMedium,
                    color = TitaniumWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Never record without full knowledge and mutual consent.\n" +
                            "2. No covert, secret, or background camera recording.\n" +
                            "3. Recording indicators and timers are always prominently visible.\n" +
                            "4. Non-surveillance: recording halts instantly on screen lock or backgrounding.\n" +
                            "5. Respect dignity and personal boundaries at all times.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (isAcknowledged) TitaniumSilver else BorderSubtle, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .clickable { onAcknowledgeToggled(!isAcknowledged) },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (isAcknowledged) KeypadBg else TitaniumSurfaceGlass)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAcknowledged,
                    onCheckedChange = onAcknowledgeToggled,
                    colors = CheckboxDefaults.colors(
                        checkedColor = TitaniumWhite,
                        checkmarkColor = TitaniumTextDark,
                        uncheckedColor = TextTertiary
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "I understand and commit to recording only with explicit, voluntary mutual consent.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isAcknowledged) TitaniumWhite else TextSecondary,
                    fontWeight = if (isAcknowledged) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }
        }
    }
}
