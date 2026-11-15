package com.privatevault.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privatevault.app.domain.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()

    fun onNextPage() {
        val current = _uiState.value.currentPage
        if (current < _uiState.value.totalPages - 1) {
            _uiState.update { it.copy(currentPage = current + 1) }
        } else {
            completeOnboarding()
        }
    }

    fun onPreviousPage() {
        val current = _uiState.value.currentPage
        if (current > 0) {
            _uiState.update { it.copy(currentPage = current - 1) }
        }
    }

    fun on18PlusToggled(checked: Boolean) {
        _uiState.update { it.copy(is18PlusConfirmed = checked) }
    }

    fun onConsentToggled(checked: Boolean) {
        _uiState.update { it.copy(isConsentAcknowledged = checked) }
    }

    fun onLocalStorageToggled(checked: Boolean) {
        _uiState.update { it.copy(isLocalStorageUnderstood = checked) }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            preferenceRepository.set18PlusConfirmed(_uiState.value.is18PlusConfirmed)
            preferenceRepository.setOnboardingCompleted(true)
            preferenceRepository.setVaultLocked(false)
            _uiState.update { it.copy(isCompleted = true) }
        }
    }
}
