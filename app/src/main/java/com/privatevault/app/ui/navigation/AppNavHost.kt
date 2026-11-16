package com.privatevault.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.privatevault.app.core.security.BiometricHelper
import com.privatevault.app.core.security.EncryptionService
import com.privatevault.app.ui.account.AccountScreen
import com.privatevault.app.ui.camera.CameraScreen
import com.privatevault.app.ui.home.HomeScreen
import com.privatevault.app.ui.lock.LockScreen
import com.privatevault.app.ui.onboarding.OnboardingScreen
import com.privatevault.app.ui.settings.SecuritySettingsScreen
import com.privatevault.app.ui.sharing.PrivateSharingScreen
import com.privatevault.app.ui.vault.VaultScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    activity: FragmentActivity,
    biometricHelper: BiometricHelper = rememberBiometricHelper(activity),
    encryptionService: EncryptionService = rememberEncryptionService(activity)
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Camera.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Lock.route) {
            LockScreen(
                activity = activity,
                biometricHelper = biometricHelper,
                onUnlocked = {
                    navController.navigate(Screen.Camera.route) {
                        popUpTo(Screen.Lock.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCamera = { navController.navigate(Screen.Camera.route) },
                onNavigateToVault = { navController.navigate(Screen.Vault.route) },
                onNavigateToSharing = { navController.navigate(Screen.Sharing.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                onLockNow = {
                    navController.navigate(Screen.Lock.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onNavigateToVault = { navController.navigate(Screen.Vault.route) },
                onNavigateToSharing = { navController.navigate(Screen.Sharing.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                onLockNow = {
                    navController.navigate(Screen.Lock.route) {
                        popUpTo(Screen.Camera.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Vault.route) {
            VaultScreen(
                onBack = { navController.popBackStack() },
                onNavigateToCamera = { navController.popBackStack() },
                encryptionService = encryptionService
            )
        }

        composable(Screen.Sharing.route) {
            PrivateSharingScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Account.route) {
            AccountScreen(
                onBack = { navController.popBackStack() },
                onAccountDeleted = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SecuritySettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun rememberBiometricHelper(activity: FragmentActivity): BiometricHelper {
    return androidx.compose.runtime.remember { BiometricHelper(activity.applicationContext) }
}

@Composable
private fun rememberEncryptionService(activity: FragmentActivity): EncryptionService {
    return androidx.compose.runtime.remember {
        val keystoreManager = com.privatevault.app.core.security.KeystoreManager()
        keystoreManager.initializeMasterKey()
        EncryptionService(keystoreManager, activity.applicationContext)
    }
}
