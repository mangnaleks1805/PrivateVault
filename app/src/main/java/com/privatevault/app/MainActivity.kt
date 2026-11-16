package com.privatevault.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import com.privatevault.app.ui.theme.TitaniumCanvas
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
        // SECURITY REQUIREMENT: FLAG_SECURE & RECENTS SHIELDING
        // Prevents screen recording, ordinary screenshots, and app switcher
        // preview leakage across the entire application lifecycle.
        // ═══════════════════════════════════════════════════════════════════
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // API 33+ (Android 13+) recents task-switcher screenshot defense
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            setRecentsScreenshotEnabled(false)
        }

        // Initialize hardware Keystore master key
        keystoreManager.initializeMasterKey()

        enableEdgeToEdge()

        setContent {
            PrivateVaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TitaniumCanvas
                ) {
                    val isOnboardingCompleted by preferenceRepository.isOnboardingCompleted
                        .collectAsState(initial = null)
                    val isVaultLocked by preferenceRepository.isVaultLocked
                        .collectAsState(initial = null)

                    // Wait until DataStore has read the true persisted state from disk
                    if (isOnboardingCompleted != null && isVaultLocked != null) {
                        val navController = rememberNavController()

                        val startDestination = when {
                            isOnboardingCompleted == false -> Screen.Onboarding.route
                            isVaultLocked == true -> Screen.Lock.route
                            else -> Screen.Camera.route
                        }

                        AppNavHost(
                            navController = navController,
                            startDestination = startDestination,
                            activity = this@MainActivity
                        )
                    } else {
                        // Clean dark transition surface while DataStore reads persistent state
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(TitaniumCanvas)
                        )
                    }
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
