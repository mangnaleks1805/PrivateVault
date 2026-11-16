package com.privatevault.app.ui.onboarding.pages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatevault.app.ui.theme.*

@Composable
fun PrivacyPrinciplesPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text(
            text = "PrivateLens",
            style = MaterialTheme.typography.headlineLarge,
            color = TitaniumWhite,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Hardware-encrypted, local-first camera and vault built for true privacy.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            lineHeight = 22.sp
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
            title = "Isolated from Gallery",
            description = "Your recordings will never appear in Google Photos, device Gallery, or public Android storage."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrincipleCard(
            title = "No Ads or Tracking",
            description = "Zero third-party trackers, zero advertising SDKs, and zero media analytics."
        )
    }
}

@Composable
private fun PrincipleCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TitaniumSurfaceGlass)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TitaniumWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
