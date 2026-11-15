package com.privatevault.app.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Lock : Screen("lock")
    data object Home : Screen("home")
    data object Camera : Screen("camera")
    data object Vault : Screen("vault")
    data object Settings : Screen("settings")
    data object Sharing : Screen("sharing")
    data object Account : Screen("account")
}
