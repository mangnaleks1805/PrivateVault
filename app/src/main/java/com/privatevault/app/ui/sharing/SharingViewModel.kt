package com.privatevault.app.ui.sharing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privatevault.app.domain.model.MediaItem
import com.privatevault.app.domain.model.SharingPermission
import com.privatevault.app.domain.model.SubscriptionTier
import com.privatevault.app.domain.repository.AccountRepository
import com.privatevault.app.domain.repository.BillingRepository
import com.privatevault.app.domain.repository.MediaRepository
import com.privatevault.app.domain.repository.SharingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharingViewModel @Inject constructor(
    private val sharingRepository: SharingRepository,
    private val mediaRepository: MediaRepository,
    private val billingRepository: BillingRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    val activePermissions: StateFlow<List<SharingPermission>> =
        sharingRepository.activePermissions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mediaItems: StateFlow<List<MediaItem>> =
        mediaRepository.getAllActiveMedia()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptionTier: StateFlow<SubscriptionTier> =
        billingRepository.subscriptionTier
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SubscriptionTier.FREE)

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun createInvitation(
        mediaId: String,
        recipientId: String,
        durationHours: Long,
        maxSessions: Int?
    ) {
        viewModelScope.launch {
            val tier = subscriptionTier.value
            val currentCount = activePermissions.value.size

            if (!billingRepository.canCreateSharePermission(currentCount, tier)) {
                _statusMessage.value = "Free tier limit reached (max 3 active shares). Upgrade to Premium for unlimited private sharing."
                return@launch
            }

            if (recipientId.isBlank()) {
                _statusMessage.value = "Please enter a valid Recipient Account ID."
                return@launch
            }

            val result = sharingRepository.createInvitation(
                mediaId = mediaId,
                recipientAccountId = recipientId,
                durationHours = durationHours,
                maxSessions = maxSessions
            )

            result.onSuccess {
                _statusMessage.value = "Invitation created! Shared with recipient: $recipientId"
            }.onFailure { err ->
                _statusMessage.value = "Failed to create invitation: ${err.localizedMessage}"
            }
        }
    }

    fun revokePermission(shareId: String) {
        viewModelScope.launch {
            val result = sharingRepository.revokePermission(shareId)
            result.onSuccess {
                _statusMessage.value = "Access revoked immediately. Recipient cannot view this media."
            }.onFailure { err ->
                _statusMessage.value = "Revocation failed: ${err.localizedMessage}"
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
