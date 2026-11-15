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
fun LocalStorageExplainerPage(
    isUnderstood: Boolean,
    onUnderstoodToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Local Storage Contract",
            style = MaterialTheme.typography.headlineLarge,
            color = AccentCyan
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your media never leaves this device unless you choose private peer-to-peer sharing.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Key things to understand:\n\n" +
                    "• No Cloud Recovery: If you uninstall the app or clear app data, encrypted files cannot be restored by us.\n" +
                    "• Hardware Bound: Vault keys are held in your device's hardware Keystore.\n" +
                    "• OS Backup Excluded: Vault data is excluded from Google Drive backups to prevent unauthorized cloud extraction.\n" +
                    "• Permissions: Camera and microphone permissions will be requested only when you open the recording interface.",
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
                checked = isUnderstood,
                onCheckedChange = onUnderstoodToggled,
                colors = CheckboxDefaults.colors(
                    checkedColor = AccentEmerald,
                    checkmarkColor = TextPrimaryDark
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "I understand that vault media is stored locally only and cannot be recovered if uninstalled.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryDark
            )
        }
    }
}
