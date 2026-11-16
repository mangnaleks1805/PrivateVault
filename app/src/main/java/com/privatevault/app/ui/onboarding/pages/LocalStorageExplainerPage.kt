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
fun LocalStorageExplainerPage(
    isUnderstood: Boolean,
    onUnderstoodToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text(
            text = "Storage & Recovery",
            style = MaterialTheme.typography.headlineLarge,
            color = TitaniumWhite,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your media never leaves this device. There is no cloud server backup.",
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
                    text = "Hardware Key Architecture",
                    style = MaterialTheme.typography.titleMedium,
                    color = TitaniumWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Zero Cloud Recovery: If this device is lost, broken, or the app is uninstalled, encrypted files cannot be recovered by anyone.\n" +
                            "• Hardware Keystore: Encryption keys are generated inside your phone's secure hardware chip.\n" +
                            "• Cloud Backup Excluded: Stored in noBackupFilesDir to prevent silent Google Drive extraction.\n" +
                            "• Isolated Sandbox: Gallery and third-party apps cannot detect or scan your recordings.",
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
                .border(1.dp, if (isUnderstood) TitaniumSilver else BorderSubtle, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .clickable { onUnderstoodToggled(!isUnderstood) },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (isUnderstood) KeypadBg else TitaniumSurfaceGlass)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isUnderstood,
                    onCheckedChange = onUnderstoodToggled,
                    colors = CheckboxDefaults.colors(
                        checkedColor = TitaniumWhite,
                        checkmarkColor = TitaniumTextDark,
                        uncheckedColor = TextTertiary
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "I understand that vault media is stored locally only and cannot be recovered if uninstalled.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnderstood) TitaniumWhite else TextSecondary,
                    fontWeight = if (isUnderstood) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }
        }
    }
}
