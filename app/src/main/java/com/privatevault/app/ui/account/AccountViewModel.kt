package com.privatevault.app.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privatevault.app.domain.model.SubscriptionTier
import com.privatevault.app.domain.model.UserAccount
import com.privatevault.app.domain.repository.AccountRepository
import com.privatevault.app.domain.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val billingRepository: BillingRepository
) : ViewModel() {

    val account: StateFlow<UserAccount?> =
        accountRepository.currentAccount
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        // Auto-initialize account if not present
        viewModelScope.launch {
            if (account.value == null) {
                accountRepository.createAccount("local_device_owner")
            }
        }
    }

    fun upgradeTier(tier: SubscriptionTier) {
        viewModelScope.launch {
            val result = billingRepository.purchaseSubscription(tier)
            result.onSuccess {
                _statusMessage.value = "Subscription upgraded to ${tier.name} successfully!"
            }.onFailure { err ->
                _statusMessage.value = "Purchase failed: ${err.localizedMessage}"
            }
        }
    }

    fun deleteAccount(deleteLocalVault: Boolean, onCompleted: () -> Unit) {
        viewModelScope.launch {
            accountRepository.deleteAccountPermanently(deleteLocalVault)
            _statusMessage.value = "Account deleted permanently."
            onCompleted()
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
