package com.privatevault.app.ui.onboarding.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.privatevault.app.ui.theme.AccentCyan
import com.privatevault.app.ui.theme.Charcoal700
import com.privatevault.app.ui.theme.Charcoal800
import com.privatevault.app.ui.theme.TextPrimaryDark
import com.privatevault.app.ui.theme.TextSecondaryDark

@Composable
fun PrivacyPrinciplesPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Privacy & Security First",
            style = MaterialTheme.typography.headlineLarge,
            color = AccentCyan
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome to Private Vault — a secure, local-only personal media vault designed for utmost privacy.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrincipleCard(
            title = "Zero Cloud Backup",
            description = "No automatic cloud uploads. Your recordings stay exclusively on this physical device."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrincipleCard(
            title = "Hardware Keystore Encryption",
            description = "Every photo and video is encrypted with a unique 256-bit AES-GCM key backed by the Android Keystore."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrincipleCard(
            title = "No Ads or Tracking",
            description = "Zero third-party trackers, zero advertising SDKs, and zero media analytics."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrincipleCard(
            title = "Isolated from Gallery",
            description = "Your recordings will never appear in Google Photos, device Gallery, or public storage."
        )
    }
}

@Composable
private fun PrincipleCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Charcoal800)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark
            )
        }
    }
}
