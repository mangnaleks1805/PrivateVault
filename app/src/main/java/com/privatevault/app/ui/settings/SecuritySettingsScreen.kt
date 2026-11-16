package com.privatevault.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatevault.app.ui.theme.*

@Composable
fun SecuritySettingsScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimaryDark
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Security & Hardening",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hardware Security & Cryptographic Boundaries",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentCyan
                )
            }
        }

        SettingCard(
            icon = Icons.Default.Key,
            title = "Hardware Keystore Master Key",
            status = "ACTIVE (AES-256-GCM)",
            statusColor = AccentEmerald,
            description = "Master DEK wrapping keys are generated and isolated inside device hardware (Titan M / Secure Element). Raw keys are never accessible to app memory or saved to disk."
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingCard(
            icon = Icons.Default.Shield,
            title = "Theft & Brute-Force Rate Limiting",
            status = "HARDWARE ENFORCED",
            statusColor = AccentEmerald,
            description = "Keys cannot be extracted without Master PIN or enrolled Biometrics. Hardware chip enforces exponential lockouts after 5 failed attempts (No arbitrary 48h deletion timer needed)."
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingCard(
            icon = Icons.Default.Security,
            title = "FLAG_SECURE Screen Shield",
            status = "ENFORCED ON ALL SCREENS",
            statusColor = AccentCyan,
            description = "Blocks third-party screen recorders, screenshots, and Android recents snapshots. Protects in-flight views inside application memory."
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingCard(
            icon = Icons.Default.PhonelinkSetup,
            title = "Direct Device Migration",
            status = "LOCAL WI-FI DIRECT",
            statusColor = AccentCyan,
            description = "Transfer entire vault to a new phone via direct encrypted Wi-Fi. Old device executes Titan M crypto-shredding to make old flash memory indecipherable."
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingCard(
            icon = Icons.Default.LinkOff,
            title = "Dual-Key Breakup Kill Switch",
            status = "READY ON DEMAND",
            statusColor = AmberSafety,
            description = "Either partner can instantly sever the Duo link. Destroys the shared key fragment, rendering all shared media permanent unreadable ciphertext."
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Back to Security Hub", color = TextPrimaryDark)
        }
    }
}

@Composable
private fun SettingCard(
    icon: ImageVector,
    title: String,
    status: String,
    statusColor: androidx.compose.ui.graphics.Color,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal800)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: $status",
                style = MaterialTheme.typography.labelSmall,
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                lineHeight = 18.sp
            )
        }
    }
}
