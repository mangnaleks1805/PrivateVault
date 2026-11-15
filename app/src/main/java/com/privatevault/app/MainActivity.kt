package com.privatevault.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.privatevault.app.core.security.KeystoreManager
import com.privatevault.app.domain.repository.PreferenceRepository
import com.privatevault.app.ui.navigation.AppNavHost
import com.privatevault.app.ui.navigation.Screen
import com.privatevault.app.ui.theme.Charcoal900
import com.privatevault.app.ui.theme.PrivateVaultTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var keystoreManager: KeystoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ═══════════════════════════════════════════════════════════════════
        // SECURITY REQUIREMENT: FLAG_SECURE
        // Prevents screen recording, ordinary screenshots, and app switcher
        // preview leakage across the entire application lifecycle.
        // ═══════════════════════════════════════════════════════════════════
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Initialize hardware Keystore master key
        keystoreManager.initializeMasterKey()

        enableEdgeToEdge()

        setContent {
            PrivateVaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Charcoal900
                ) {
                    val navController = rememberNavController()

                    val isOnboardingCompleted by preferenceRepository.isOnboardingCompleted
                        .collectAsState(initial = false)
                    val isVaultLocked by preferenceRepository.isVaultLocked
                        .collectAsState(initial = true)

                    val startDestination = when {
                        !isOnboardingCompleted -> Screen.Onboarding.route
                        isVaultLocked -> Screen.Lock.route
                        else -> Screen.Home.route
                    }

                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        activity = this@MainActivity
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Lock vault automatically whenever the application leaves foreground
        lifecycleScope.launch {
            if (preferenceRepository.isOnboardingCompleted.first()) {
                preferenceRepository.setVaultLocked(true)
            }
        }
    }
}
