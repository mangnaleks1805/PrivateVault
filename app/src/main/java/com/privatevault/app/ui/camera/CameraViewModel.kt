package com.privatevault.app.ui.camera

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privatevault.app.core.camera.CameraManager
import com.privatevault.app.core.camera.CameraRecordingState
import com.privatevault.app.core.camera.CaptureMode
import com.privatevault.app.core.camera.LensFacing
import com.privatevault.app.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

data class CameraUiState(
    val hasCameraPermission: Boolean = false,
    val hasAudioPermission: Boolean = false,
    val isAudioEnabled: Boolean = true,
    val isEncryptingAndSaving: Boolean = false,
    val savedMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    val cameraManager: CameraManager,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    val recordingState: StateFlow<CameraRecordingState> = cameraManager.recordingState
    val currentLens: StateFlow<LensFacing> = cameraManager.currentLensFacing
    val captureMode: StateFlow<CaptureMode> = cameraManager.captureMode
    val zoomState: StateFlow<com.privatevault.app.core.camera.CameraZoomState> = cameraManager.zoomState

    fun onPermissionsResult(cameraGranted: Boolean, audioGranted: Boolean) {
        _uiState.update {
            it.copy(
                hasCameraPermission = cameraGranted,
                hasAudioPermission = audioGranted
            )
        }
    }

    fun toggleAudio() {
        _uiState.update { it.copy(isAudioEnabled = !it.isAudioEnabled) }
    }

    fun switchLens() {
        cameraManager.switchLens()
    }

    fun setZoomRatio(ratio: Float) {
        cameraManager.setZoomRatio(ratio)
    }

    fun setCaptureMode(mode: CaptureMode) {
        cameraManager.setCaptureMode(mode)
    }

    fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        cameraManager.bindCamera(lifecycleOwner, previewView)
    }

    fun capturePhoto(onSuccess: () -> Unit) {
        if (_uiState.value.isEncryptingAndSaving) return

        _uiState.update { it.copy(isEncryptingAndSaving = true, errorMessage = null) }
        cameraManager.takePhoto(
            onSuccess = { tempFile ->
                viewModelScope.launch {
                    runCatching {
                        mediaRepository.saveCapturedPhoto(tempFile)
                        _uiState.update {
                            it.copy(
                                isEncryptingAndSaving = false,
                                savedMessage = "Encrypted photo saved to vault!"
                            )
                        }
                        onSuccess()
                    }.onFailure { ex ->
                        Timber.e(ex, "Failed to encrypt photo")
                        _uiState.update {
                            it.copy(
                                isEncryptingAndSaving = false,
                                errorMessage = "Failed to encrypt and store photo."
                            )
                        }
                    }
                }
            },
            onError = { ex ->
                Timber.e(ex, "Photo capture error")
                _uiState.update {
                    it.copy(
                        isEncryptingAndSaving = false,
                        errorMessage = "Camera capture failed: ${ex.message}"
                    )
                }
            }
        )
    }

    fun startVideoRecording() {
        val recordAudio = _uiState.value.isAudioEnabled && _uiState.value.hasAudioPermission
        cameraManager.startRecording(
            recordAudio = recordAudio,
            onVideoSaved = { tempFile, durationMs ->
                viewModelScope.launch {
                    _uiState.update { it.copy(isEncryptingAndSaving = true) }
                    runCatching {
                        mediaRepository.saveCapturedVideo(tempFile, durationMs)
                        _uiState.update {
                            it.copy(
                                isEncryptingAndSaving = false,
                                savedMessage = "Encrypted video saved to vault!"
                            )
                        }
                    }.onFailure { ex ->
                        Timber.e(ex, "Failed to encrypt video")
                        _uiState.update {
                            it.copy(
                                isEncryptingAndSaving = false,
                                errorMessage = "Failed to encrypt and store video."
                            )
                        }
                    }
                }
            },
            onError = { ex ->
                Timber.e(ex, "Video recording error")
                _uiState.update {
                    it.copy(
                        errorMessage = "Recording error: ${ex.message}"
                    )
                }
            }
        )
    }

    fun pauseVideoRecording() {
        cameraManager.pauseRecording()
    }

    fun resumeVideoRecording() {
        cameraManager.resumeRecording()
    }

    fun stopVideoRecording() {
        cameraManager.stopRecording()
    }

    fun clearMessages() {
        _uiState.update { it.copy(savedMessage = null, errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        cameraManager.stopRecordingOnLifecyclePause()
    }
}
