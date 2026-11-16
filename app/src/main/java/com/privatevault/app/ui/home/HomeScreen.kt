package com.privatevault.app.ui.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatevault.app.ui.theme.*

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
            .background(TitaniumCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(TitaniumSilver)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PrivateLens",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TitaniumWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "HARDWARE-ENCRYPTED LOCAL VAULT",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    letterSpacing = 1.2.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = TitaniumSurface,
                modifier = Modifier
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
            ) {
                OutlinedButton(
                    onClick = onLockNow,
                    border = null,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TitaniumSilver
                    ),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = TitaniumSilver,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Lock",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TitaniumSilver
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Hero Action: Secure Camera
        TitaniumActionCard(
            title = "Pro Camera",
            subtitle = "Consensual photo & 60fps video directly to local encrypted memory.",
            badge = "HARDWARE SECURE",
            icon = Icons.Default.CameraAlt,
            isHero = true,
            onClick = onNavigateToCamera
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Vault Card
        TitaniumActionCard(
            title = "Encrypted Vault",
            subtitle = "Organize, search, and view zero-cloud protected recordings.",
            icon = Icons.Default.PhotoLibrary,
            onClick = onNavigateToVault
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Controlled Sharing
        TitaniumActionCard(
            title = "Controlled Private Sharing",
            subtitle = "P2P ephemeral viewing, duration expiry, zero cloud storage.",
            icon = Icons.Default.Share,
            onClick = onNavigateToSharing
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Security & Audit
        TitaniumActionCard(
            title = "Security & Keystore Status",
            subtitle = "Biometric lock, encryption status, and safety audits.",
            icon = Icons.Default.Security,
            onClick = onNavigateToSettings
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Account & Profile
        TitaniumActionCard(
            title = "Anonymous Profile",
            subtitle = "Device identity, storage capacity, and emergency panic wipe.",
            icon = Icons.Default.Person,
            onClick = onNavigateToAccount
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TitaniumActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String? = null,
    isHero: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isHero) BorderHighlight else BorderSubtle,
                RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHero) TitaniumSurfaceGlass else TitaniumCard
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(KeypadBg)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = TitaniumWhite,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (badge != null) {
                    Text(
                        text = badge,
                        color = TitaniumSilver,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TitaniumWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
