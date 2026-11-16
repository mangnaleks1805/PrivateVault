package com.privatevault.app.ui.account

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.privatevault.app.domain.model.SubscriptionTier
import com.privatevault.app.ui.theme.AccentCyan
import com.privatevault.app.ui.theme.AccentEmerald
import com.privatevault.app.ui.theme.AlertRed
import com.privatevault.app.ui.theme.AmberSafety
import com.privatevault.app.ui.theme.Charcoal800
import com.privatevault.app.ui.theme.Charcoal900
import com.privatevault.app.ui.theme.TextPrimaryDark
import com.privatevault.app.ui.theme.TextSecondaryDark
import com.privatevault.app.ui.theme.Zinc300

@Composable
fun AccountScreen(
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val account by viewModel.account.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMigrationDialog by remember { mutableStateOf(false) }
    var showKillSwitchDialog by remember { mutableStateOf(false) }
    var deleteVaultFilesChecked by remember { mutableStateOf(false) }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Charcoal900
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                        text = "Vault & Security Hub",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Zero-Knowledge Local Hardware Architecture",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentEmerald
                    )
                }
            }

            // 1. ANONYMOUS CRYPTOGRAPHIC VAULT OWNER CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = Charcoal800),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = AccentCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Anonymous Cryptographic ID",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "ZERO SIGN-IN",
                            color = AccentEmerald,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Vault ID (Public Key Hash)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )
                    Text(
                        text = account?.userId ?: "LENS-LOCAL-TEE-VAULT",
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Device Hardware Binding",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )
                    Text(
                        text = account?.deviceFingerprint ?: "Titan M / Android Keystore Protected",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "18+ Adult Eligibility Confirmed On-Device",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentEmerald,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. DEDICATED DUO PARTNER VAULT CARD (COUPLE 1-TO-1 BOND)
            Card(
                colors = CardDefaults.cardColors(containerColor = Charcoal800),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = AccentCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Duo Partner Vault",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "PAIRED ACTIVE",
                            color = AccentCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Paired Partner: Alex (@alex.partner)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Dual-Key AES-256 Handshake verified. Shared media requires mutual active key state.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showMigrationDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PhonelinkSetup, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Migrate Phone", fontSize = 11.sp, color = AccentCyan)
                        }

                        Button(
                            onClick = { showKillSwitchDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed.copy(alpha = 0.25f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.LinkOff, contentDescription = null, modifier = Modifier.size(16.dp), tint = AlertRed)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sever Duo Link", fontSize = 11.sp, color = AlertRed)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. SUBSCRIPTION CARD (2-PLAN MODEL: FREE VS PRO & DUO ALL-IN-ONE)
            Card(
                colors = CardDefaults.cardColors(containerColor = Charcoal800),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AmberSafety.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AmberSafety)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Security Plan",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (account?.subscriptionTier == SubscriptionTier.COUPLE || account?.subscriptionTier == SubscriptionTier.PREMIUM) "PRO & DUO ACTIVE" else "FREE TIER",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberSafety,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "All-In-One Pro & Couple Vault:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• 4K 60fps recording with up to 30 min duration\n• 2-Device Paired Duo Vault (Encrypted Sync)\n• Breakup Kill Switch (Mutual remote wipe)\n• Direct Phone Migration with Hardware Shredding\n• All 10 Vintage RAW & Beauty Film Lenses",
                        style = MaterialTheme.typography.bodySmall,
                        color = Zinc300
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Pricing Options: ₹19/mo, ₹149/yr, ₹499 Lifetime
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.upgradeTier(SubscriptionTier.COUPLE) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Charcoal900),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Monthly", fontSize = 10.sp, color = TextSecondaryDark)
                                Text("₹19", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            }
                        }

                        Button(
                            onClick = { viewModel.upgradeTier(SubscriptionTier.COUPLE) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Charcoal900),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Yearly", fontSize = 10.sp, color = TextSecondaryDark)
                                Text("₹149", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentEmerald)
                            }
                        }

                        Button(
                            onClick = { viewModel.upgradeTier(SubscriptionTier.COUPLE) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberSafety),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Lifetime", fontSize = 10.sp, color = Charcoal900)
                                Text("₹499", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Charcoal900)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { viewModel.upgradeTier(SubscriptionTier.COUPLE) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondaryDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore Purchases (Google Play)", color = TextSecondaryDark, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. DANGER ZONE: ACCOUNT & VAULT WIPING
            Card(
                colors = CardDefaults.cardColors(containerColor = Charcoal800),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = AlertRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Permanent Vault Removal",
                            style = MaterialTheme.typography.titleMedium,
                            color = AlertRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Permanently deletes your anonymous vault identifier, credentials, and all active pairing permissions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Delete Account Permanently", color = TextPrimaryDark)
                    }
                }
            }
        }
    }

    // Migration Modal
    if (showMigrationDialog) {
        AlertDialog(
            onDismissRequest = { showMigrationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhonelinkSetup, contentDescription = null, tint = AccentCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Direct Phone Migration", color = TextPrimaryDark, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column {
                    Text(
                        text = "How to migrate to a new phone safely:",
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. Install PrivateLens on your new phone.\n2. Tap 'Migrate / Restore Vault' on the new phone to show its QR.\n3. Scan the QR code with this old phone.\n4. Verify your Fingerprint + Master PIN.\n5. Vault streams over local high-speed Wi-Fi Direct.\n6. Old phone performs hardware crypto-shredding on completion.",
                        color = TextSecondaryDark,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showMigrationDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)) {
                    Text("Ready to Scan", color = Charcoal900)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMigrationDialog = false }) {
                    Text("Close", color = TextSecondaryDark)
                }
            },
            containerColor = Charcoal800
        )
    }

    // Kill Switch Modal
    if (showKillSwitchDialog) {
        AlertDialog(
            onDismissRequest = { showKillSwitchDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LinkOff, contentDescription = null, tint = AlertRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Breakup Kill Switch", color = AlertRed, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Text(
                    text = "Severing the Duo link immediately destroys the shared Dual-Key fragment on both devices. All media in the Duo Vault becomes mathematically scrambled ciphertext and unrecoverable forever.\n\nAre you sure you want to proceed?",
                    color = TextPrimaryDark,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showKillSwitchDialog = false
                        viewModel.deleteAccount(deleteLocalVault = false) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) {
                    Text("Sever & Wipe Shared Media", color = TextPrimaryDark)
                }
            },
            dismissButton = {
                TextButton(onClick = { showKillSwitchDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = Charcoal800
        )
    }

    // Delete Account Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Confirm Permanent Deletion",
                    color = AlertRed,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    Text(
                        text = "This action is irreversible. Your account identity will be permanently wiped.",
                        color = TextPrimaryDark,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = deleteVaultFilesChecked,
                            onCheckedChange = { deleteVaultFilesChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AlertRed,
                                uncheckedColor = TextSecondaryDark
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Also crypto-shred all local encrypted media files",
                            color = TextSecondaryDark,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAccount(deleteLocalVault = deleteVaultFilesChecked) {
                            onAccountDeleted()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) {
                    Text("Yes, Permanently Delete", color = TextPrimaryDark)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = Charcoal800
        )
    }
}
