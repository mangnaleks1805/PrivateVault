package com.privatevault.app.ui.onboarding

data class OnboardingState(
    val currentPage: Int = 0,
    val totalPages: Int = 4,
    val is18PlusConfirmed: Boolean = false,
    val isConsentAcknowledged: Boolean = false,
    val isLocalStorageUnderstood: Boolean = false,
    val isCompleted: Boolean = false
) {
    val canProceed: Boolean
        get() = when (currentPage) {
            1 -> is18PlusConfirmed
            2 -> isConsentAcknowledged
            3 -> isLocalStorageUnderstood
            else -> true
        }
}
