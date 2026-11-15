package com.privatevault.app.ui.vault

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privatevault.app.domain.model.MediaItem
import com.privatevault.app.domain.model.MediaType
import com.privatevault.app.domain.model.VaultFolder
import com.privatevault.app.domain.repository.MediaRepository
import com.privatevault.app.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

enum class VaultFilter {
    ALL,
    PHOTOS,
    VIDEOS
}

data class VaultUiState(
    val filter: VaultFilter = VaultFilter.ALL,
    val selectedFolderId: String? = null,
    val searchQuery: String = "",
    val selectedMediaItem: MediaItem? = null,
    val isPlayingVideo: Boolean = false,
    val selectedMediaIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
    val storageUsageBytes: Long = 0L,
    val showCreateFolderDialog: Boolean = false
)

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val vaultRepository: VaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    val folders: StateFlow<List<VaultFolder>> = vaultRepository.getAllFolders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // In-memory cache of decrypted thumbnails — never cached to disk
    val thumbnailCache = ConcurrentHashMap<String, Bitmap>()

    val mediaItems: StateFlow<List<MediaItem>> = combine(
        mediaRepository.getAllActiveMedia(),
        _uiState
    ) { allMedia, state ->
        allMedia.filter { item ->
            val matchesFolder = if (state.selectedFolderId != null) {
                item.folderId == state.selectedFolderId
            } else true

            val matchesFilter = when (state.filter) {
                VaultFilter.ALL -> true
                VaultFilter.PHOTOS -> item.mediaType == MediaType.PHOTO
                VaultFilter.VIDEOS -> item.mediaType == MediaType.VIDEO
            }

            matchesFolder && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Track vault storage usage
        viewModelScope.launch {
            mediaRepository.getTotalVaultSizeBytes().collect { size ->
                _uiState.update { it.copy(storageUsageBytes = size ?: 0L) }
            }
        }
    }

    fun setFilter(filter: VaultFilter) {
        _uiState.update { it.copy(filter = filter, selectedFolderId = null) }
    }

    fun selectFolder(folderId: String?) {
        _uiState.update { it.copy(selectedFolderId = folderId) }
    }

    fun openMedia(item: MediaItem) {
        _uiState.update {
            it.copy(
                selectedMediaItem = item,
                isPlayingVideo = item.mediaType == MediaType.VIDEO
            )
        }
    }

    fun closeMedia() {
        _uiState.update {
            it.copy(
                selectedMediaItem = null,
                isPlayingVideo = false
            )
        }
    }

    fun toggleSelection(mediaId: String) {
        _uiState.update { state ->
            val newSelection = state.selectedMediaIds.toMutableSet()
            if (newSelection.contains(mediaId)) {
                newSelection.remove(mediaId)
            } else {
                newSelection.add(mediaId)
            }
            state.copy(
                selectedMediaIds = newSelection,
                isSelectionMode = newSelection.isNotEmpty()
            )
        }
    }

    fun clearSelection() {
        _uiState.update {
            it.copy(selectedMediaIds = emptySet(), isSelectionMode = false) }
    }

    fun deleteSelectedMedia() {
        val idsToDelete = _uiState.value.selectedMediaIds
        viewModelScope.launch {
            idsToDelete.forEach { id ->
                mediaRepository.deleteMedia(id)
                thumbnailCache.remove(id)
            }
            clearSelection()
        }
    }

    fun deleteSingleMedia(item: MediaItem) {
        viewModelScope.launch {
            mediaRepository.deleteMedia(item.id)
            thumbnailCache.remove(item.id)
            closeMedia()
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            vaultRepository.createFolder(name, 0, 0)
            _uiState.update { it.copy(showCreateFolderDialog = false) }
        }
    }

    fun setShowCreateFolderDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateFolderDialog = show) }
    }

    suspend fun loadThumbnail(item: MediaItem): Bitmap? {
        thumbnailCache[item.id]?.let { return it }
        val loaded = mediaRepository.loadThumbnail(item)
        if (loaded != null) {
            thumbnailCache[item.id] = loaded
        }
        return loaded
    }

    suspend fun loadFullPhoto(item: MediaItem): Bitmap? {
        return mediaRepository.loadFullPhotoBitmap(item)
    }

    suspend fun getMediaDEK(item: MediaItem): ByteArray? {
        return mediaRepository.getMediaDEK(item)
    }

    fun getEncryptedFile(item: MediaItem): File {
        return mediaRepository.getEncryptedFile(item)
    }
}
