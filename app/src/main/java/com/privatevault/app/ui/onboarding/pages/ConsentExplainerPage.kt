package com.privatevault.app.ui.onboarding.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.privatevault.app.ui.theme.AccentCyan
import com.privatevault.app.ui.theme.AccentEmerald
import com.privatevault.app.ui.theme.TextPrimaryDark
import com.privatevault.app.ui.theme.TextSecondaryDark

@Composable
fun ConsentExplainerPage(
    isAcknowledged: Boolean,
    onAcknowledgeToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Consent & Respect",
            style = MaterialTheme.typography.headlineLarge,
            color = AccentCyan
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Recording requires explicit knowledge and consent from everyone involved.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Our Non-Negotiable Core Rules:\n\n" +
                    "1. Never record without the full knowledge and consent of all participants.\n" +
                    "2. No covert, secret, or hidden camera recording.\n" +
                    "3. No recording when the screen is off or disguised as another utility.\n" +
                    "4. Recording indicators and timers are always prominently visible.\n" +
                    "5. Respect privacy, dignity, and boundaries at all times.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isAcknowledged,
                onCheckedChange = onAcknowledgeToggled,
                colors = CheckboxDefaults.colors(
                    checkedColor = AccentEmerald,
                    checkmarkColor = TextPrimaryDark
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "I understand and commit to recording only with explicit, voluntary mutual consent.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryDark
            )
        }
    }
}
