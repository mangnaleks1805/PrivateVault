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
import com.privatevault.app.ui.theme.AccentRose
import com.privatevault.app.ui.theme.TextPrimaryDark
import com.privatevault.app.ui.theme.TextSecondaryDark

@Composable
fun EligibilityConfirmPage(
    isConfirmed: Boolean,
    onConfirmToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Age Eligibility",
            style = MaterialTheme.typography.headlineLarge,
            color = AccentCyan
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "This application is designed exclusively for adults aged 18 and above.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Important Legal & Compliance Notice:\n" +
                    "• The vault is intended solely for personal, private media management by consenting adults.\n" +
                    "• Self-declaration here does not constitute formal statutory age verification.\n" +
                    "• Non-adults are strictly prohibited from using this application.\n" +
                    "• Any unlawful use or non-consensual material is strictly prohibited under applicable laws.",
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
                checked = isConfirmed,
                onCheckedChange = onConfirmToggled,
                colors = CheckboxDefaults.colors(
                    checkedColor = AccentCyan,
                    checkmarkColor = TextPrimaryDark
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "I solemnly confirm that I am at least 18 years of age and legally eligible to use this application.",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isConfirmed) TextPrimaryDark else AccentRose
            )
        }
    }
}
