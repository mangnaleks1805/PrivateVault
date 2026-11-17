package com.privatevault.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var selectedAutoLockTimeout by remember { mutableStateOf("1m") }
    var isAntiTamperWipeEnabled by remember { mutableStateOf(true) }
    var isDuressEnabled by remember { mutableStateOf(true) }
    var isUsbGuardEnabled by remember { mutableStateOf(true) }

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

        // 2. USER-CONFIGURABLE INACTIVITY AUTO-LOCK (Option 3)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Charcoal800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Inactivity Auto-Lock & RAM Purge",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select when PrivateLens purges RAM buffers and locks the vault:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Selectable timeout options: Immediately, 30s, 1m, 3m, 5m
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("immediate" to "Immediate", "30s" to "30 Sec", "1m" to "1 Min", "3m" to "3 Min", "5m" to "5 Min").forEach { (key, label) ->
                        val selected = selectedAutoLockTimeout == key
                        Surface(
                            onClick = { selectedAutoLockTimeout = key },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) AccentCyan else Charcoal900,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Charcoal900 else TextSecondaryDark
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. DURESS / DECOY VAULT CAPABILITY (Option 1)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Charcoal800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AmberSafety, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Duress / Panic Passcode (Decoy Vault)",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Switch(
                        checked = isDuressEnabled,
                        onCheckedChange = { isDuressEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberSafety, checkedTrackColor = AmberSafety.copy(alpha = 0.3f))
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "If coerced under physical threat, entering an alternate 12-char emergency passcode opens a harmless Decoy Vault with scenery/travel photos, keeping your real vault hidden.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. ANTI-TAMPER AUTO-WIPE (Option 2)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Charcoal800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = AlertRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Anti-Tamper Auto-Wipe",
                            style = MaterialTheme.typography.titleMedium,
                            color = AlertRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Switch(
                        checked = isAntiTamperWipeEnabled,
                        onCheckedChange = { isAntiTamperWipeEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AlertRed, checkedTrackColor = AlertRed.copy(alpha = 0.3f))
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "If 10 consecutive incorrect 12-character passcodes are entered, automatically shred all Titan M Keystore keys to protect files from physical extraction.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. ROOT & FRIDA HOOKING DETECTION (Option 4)
        SettingCard(
            icon = Icons.Default.BugReport,
            title = "Root & Frida Hooking Detection",
            status = "INTEGRITY ACTIVE",
            statusColor = AccentCyan,
            description = "Detects modified kernel runtimes, Magisk superuser hooks, or reverse-engineering instrumentation probing app process memory."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 6. USB DEBUGGING GUARD (Option 5)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Charcoal800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Usb, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "USB Debugging (ADB) Guard",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Switch(
                        checked = isUsbGuardEnabled,
                        onCheckedChange = { isUsbGuardEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentCyan, checkedTrackColor = AccentCyan.copy(alpha = 0.3f))
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Warns and restricts decryption when USB debugging or untrusted computer cables are connected.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 7. FLAG_SECURE SCREEN SHIELD
        SettingCard(
            icon = Icons.Default.Security,
            title = "FLAG_SECURE Anti-Spyware Shield",
            status = "ENFORCED ON ALL SCREENS",
            statusColor = AccentCyan,
            description = "Blocks third-party screen recorders, accessibility spyware, remote screen-mirroring apps, and Android task switcher snapshots."
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
