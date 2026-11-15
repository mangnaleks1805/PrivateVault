package com.privatevault.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.privatevault.app.ui.theme.AccentCyan
import com.privatevault.app.ui.theme.AccentEmerald
import com.privatevault.app.ui.theme.Charcoal800
import com.privatevault.app.ui.theme.Charcoal900
import com.privatevault.app.ui.theme.TextPrimaryDark
import com.privatevault.app.ui.theme.TextSecondaryDark

@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToVault: () -> Unit,
    onNavigateToSharing: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onLockNow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Private Vault",
                    style = MaterialTheme.typography.headlineLarge,
                    color = AccentCyan
                )
                Text(
                    text = "Encrypted • Local • Secure",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentEmerald
                )
            }

            OutlinedButton(
                onClick = onLockNow,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextSecondaryDark
                )
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Lock", tint = AccentCyan)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Lock")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ActionCard(
            title = "Secure Camera",
            subtitle = "Capture photos & videos directly into encrypted storage.",
            icon = Icons.Default.CameraAlt,
            onClick = onNavigateToCamera
        )

        Spacer(modifier = Modifier.height(14.dp))

        ActionCard(
            title = "Private Media Vault",
            subtitle = "Browse, organize, and view your encrypted recordings.",
            icon = Icons.Default.PhotoLibrary,
            onClick = onNavigateToVault
        )

        Spacer(modifier = Modifier.height(14.dp))

        ActionCard(
            title = "Controlled Private Sharing",
            subtitle = "P2P ephemeral viewing, duration expiry, zero cloud storage.",
            icon = Icons.Default.Share,
            onClick = onNavigateToSharing
        )

        Spacer(modifier = Modifier.height(14.dp))

        ActionCard(
            title = "Security & Audit",
            subtitle = "Biometric lock, encryption status, and safety audits.",
            icon = Icons.Default.Security,
            onClick = onNavigateToSettings
        )

        Spacer(modifier = Modifier.height(14.dp))

        ActionCard(
            title = "Account & Profile",
            subtitle = "Anonymous identity, subscription tier, and data wipe.",
            icon = Icons.Default.Person,
            onClick = onNavigateToAccount
        )
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Charcoal800)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AccentCyan,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
            }
        }
    }
}
