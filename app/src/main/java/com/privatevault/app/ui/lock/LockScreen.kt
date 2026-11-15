package com.privatevault.app.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.privatevault.app.core.security.BiometricAuthResult
import com.privatevault.app.core.security.BiometricHelper
import com.privatevault.app.ui.theme.AccentCyan
import com.privatevault.app.ui.theme.AccentRose
import com.privatevault.app.ui.theme.Charcoal900
import com.privatevault.app.ui.theme.TextPrimaryDark
import com.privatevault.app.ui.theme.TextSecondaryDark

@Composable
fun LockScreen(
    onUnlocked: () -> Unit,
    biometricHelper: BiometricHelper,
    activity: FragmentActivity? = null
) {
    val context = LocalContext.current
    val hostActivity = activity ?: (context as? FragmentActivity)

    var authErrorMessage by remember { mutableStateOf<String?>(null) }

    fun triggerBiometricAuth() {
        if (hostActivity != null && biometricHelper.isBiometricAvailable()) {
            biometricHelper.authenticate(
                activity = hostActivity,
                title = "Unlock Private Vault",
                subtitle = "Authenticate to decrypt vault contents"
            ) { result ->
                when (result) {
                    is BiometricAuthResult.Success -> {
                        authErrorMessage = null
                        onUnlocked()
                    }
                    is BiometricAuthResult.Error -> {
                        authErrorMessage = result.errString.toString()
                    }
                    is BiometricAuthResult.Failed -> {
                        authErrorMessage = "Biometric not recognized. Please try again."
                    }
                }
            }
        } else {
            // Direct unlock if no device lock is configured on emulator
            onUnlocked()
        }
    }

    // Auto-prompt on launch
    LaunchedEffect(Unit) {
        triggerBiometricAuth()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Vault Locked",
            tint = AccentCyan,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Vault Locked",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hardware-backed biometric or device credentials required to access encrypted recordings.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
        )

        authErrorMessage?.let { err ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = err,
                style = MaterialTheme.typography.bodySmall,
                color = AccentRose
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { triggerBiometricAuth() },
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentCyan,
                contentColor = Charcoal900
            )
        ) {
            Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Unlock Vault", fontWeight = FontWeight.Bold)
        }
    }
}
