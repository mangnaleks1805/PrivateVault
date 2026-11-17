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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
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
                    text = "Security & Hardening Hub",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Multi-Layered Cryptographic Fortress",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentCyan
                )
            }
        }

        // 1. 12-CHARACTER MIXED PASSCODE
        SettingCard(
            icon = Icons.Default.Password,
            title = "12-Character Mixed Master Passcode",
            status = "4.7 x 10^23 COMBINATIONS",
            statusColor = AccentEmerald,
            description = "High-entropy alphanumeric passcode (uppercase, lowercase, numbers, symbols). Mathematically immune to automated random dictionary guessing."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. HARDWARE KEYSTORE ISOLATION
        SettingCard(
            icon = Icons.Default.Key,
            title = "Hardware Keystore Master Key (Titan M)",
            status = "ACTIVE (AES-256-GCM)",
            statusColor = AccentEmerald,
            description = "Master DEK wrapping keys are generated inside device hardware silicon (Titan M / Secure Element). Raw keys can never be exported or dumped, even with root access."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. THEFT & ANTI-TAMPER RATE LIMITING
        SettingCard(
            icon = Icons.Default.Shield,
            title = "Hardware Exponential Lockout",
            status = "HARDWARE ENFORCED",
            statusColor = AccentEmerald,
            description = "Hardware chip freezes attempts after 5 failed tries (30s, 30m, 24h lockouts). Protects against physical phone thieves without arbitrary deletion timers."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4. FLAG_SECURE SCREEN SHIELD
        SettingCard(
            icon = Icons.Default.Security,
            title = "FLAG_SECURE Anti-Spyware Shield",
            status = "ENFORCED ON ALL SCREENS",
            statusColor = AccentCyan,
            description = "Blocks third-party screen recorders, accessibility spyware, remote screen-mirroring apps, and Android task switcher snapshots."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 5. INACTIVITY AUTO-LOCK
        SettingCard(
            icon = Icons.Default.Timer,
            title = "Inactivity Auto-Lock & RAM Clear",
            status = "30-SECOND AUTO-LOCK",
            statusColor = AccentCyan,
            description = "The second the app is minimized or idle for 30s, all decrypted RAM buffers are immediately purged from memory and the vault locks."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 6. DURESS / DECOY VAULT CAPABILITY
        SettingCard(
            icon = Icons.Default.Lock,
            title = "Duress / Panic Passcode (Decoy Vault)",
            status = "OPTIONAL ACTIVE",
            statusColor = AmberSafety,
            description = "Allows entering an alternate 12-char emergency passcode under physical coercion to open a harmless decoy vault, keeping true vault hidden."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 7. ROOT & HOOKING INTEGRITY CHECK
        SettingCard(
            icon = Icons.Default.BugReport,
            title = "Root & Frida Hooking Detection",
            status = "INTEGRITY GUARD ON",
            statusColor = AccentCyan,
            description = "Detects modified kernel runtimes, Magisk superuser hooks, or reverse-engineering instrumentation probing app process memory."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 8. DIRECT PHONE MIGRATION
        SettingCard(
            icon = Icons.Default.PhonelinkSetup,
            title = "Direct Phone Migration & Crypto-Shredding",
            status = "LOCAL WI-FI DIRECT",
            statusColor = AccentCyan,
            description = "Transfer entire vault to a new phone over direct Wi-Fi. Old device calls KeyStore.deleteEntry() to shred all keys, making old flash memory permanent scrambled noise."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 9. DUAL-KEY BREAKUP KILL SWITCH
        SettingCard(
            icon = Icons.Default.LinkOff,
            title = "Dual-Key Breakup Kill Switch",
            status = "READY ON DEMAND",
            statusColor = AlertRed,
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
