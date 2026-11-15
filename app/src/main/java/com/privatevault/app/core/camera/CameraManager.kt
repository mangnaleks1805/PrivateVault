package com.privatevault.app.core.camera

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.File
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

enum class LensFacing {
    BACK,
    FRONT
}

enum class CaptureMode {
    PHOTO,
    VIDEO
}

data class CameraRecordingState(
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val durationSeconds: Long = 0L,
    val recordedBytes: Long = 0L
)

data class CameraZoomState(
    val zoomRatio: Float = 1f,
    val minZoomRatio: Float = 1f,
    val maxZoomRatio: Float = 5f,
    val linearZoom: Float = 0f
)

@Singleton
class CameraManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val mainExecutor: Executor = ContextCompat.getMainExecutor(context)
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    private var currentLifecycleOwner: LifecycleOwner? = null
    private var currentPreviewView: PreviewView? = null

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null
    private var activeVideoTempFile: File? = null

    private val _recordingState = MutableStateFlow(CameraRecordingState())
    val recordingState: StateFlow<CameraRecordingState> = _recordingState.asStateFlow()

    private val _currentLensFacing = MutableStateFlow(LensFacing.BACK)
    val currentLensFacing: StateFlow<LensFacing> = _currentLensFacing.asStateFlow()

    private val _captureMode = MutableStateFlow(CaptureMode.PHOTO)
    val captureMode: StateFlow<CaptureMode> = _captureMode.asStateFlow()

    private val _zoomState = MutableStateFlow(CameraZoomState())
    val zoomState: StateFlow<CameraZoomState> = _zoomState.asStateFlow()

    fun switchLens() {
        if (_recordingState.value.isRecording) return // Disallow switching while recording
        _currentLensFacing.value = if (_currentLensFacing.value == LensFacing.BACK) {
            LensFacing.FRONT
        } else {
            LensFacing.BACK
        }

        // Rebind immediately if view and lifecycle are active
        val owner = currentLifecycleOwner
        val view = currentPreviewView
        if (owner != null && view != null) {
            bindCamera(owner, view)
        }
    }

    fun setCaptureMode(mode: CaptureMode) {
        if (_recordingState.value.isRecording) return
        _captureMode.value = mode
    }

    fun setZoomRatio(ratio: Float) {
        val clamped = ratio.coerceIn(_zoomState.value.minZoomRatio, _zoomState.value.maxZoomRatio)
        camera?.cameraControl?.setZoomRatio(clamped)
    }

    fun setLinearZoom(linear: Float) {
        camera?.cameraControl?.setLinearZoom(linear.coerceIn(0f, 1f))
    }

    /**
     * Binds CameraX use cases to the provided LifecycleOwner and PreviewView.
     */
    fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onCameraReady: () -> Unit = {}
    ) {
        currentLifecycleOwner = lifecycleOwner
        currentPreviewView = previewView

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val provider = cameraProviderFuture.get()
            cameraProvider = provider

            val cameraSelector = if (_currentLensFacing.value == LensFacing.BACK) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            if (!provider.hasCamera(cameraSelector)) {
                Timber.w("Device does not possess camera facing: ${_currentLensFacing.value}")
                return@addListener
            }

            // Preview usecase
            preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            // ImageCapture usecase
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // VideoCapture usecase
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            runCatching {
                provider.unbindAll()
                val boundCamera = provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    videoCapture
                )
                camera = boundCamera

                // Observe hardware zoom state
                boundCamera.cameraInfo.zoomState.observe(lifecycleOwner) { state ->
                    _zoomState.value = CameraZoomState(
                        zoomRatio = state.zoomRatio,
                        minZoomRatio = state.minZoomRatio,
                        maxZoomRatio = state.maxZoomRatio,
                        linearZoom = state.linearZoom
                    )
                }

                // Attach pinch-to-zoom and tap-to-focus
                setupTouchGestures(previewView, boundCamera)

                onCameraReady()
            }.onFailure { ex ->
                Timber.e(ex, "Failed to bind CameraX use cases for ${_currentLensFacing.value}")
            }
        }, mainExecutor)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchGestures(previewView: PreviewView, cameraInstance: Camera) {
        val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentRatio = cameraInstance.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                val delta = detector.scaleFactor
                val targetRatio = (currentRatio * delta).coerceIn(
                    _zoomState.value.minZoomRatio,
                    _zoomState.value.maxZoomRatio
                )
                cameraInstance.cameraControl.setZoomRatio(targetRatio)
                return true
            }
        })

        previewView.setOnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP && !scaleDetector.isInProgress) {
                // Tap to focus
                val factory = previewView.meteringPointFactory
                val point = factory.createPoint(event.x, event.y)
                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    .setAutoCancelDuration(3, TimeUnit.SECONDS)
                    .build()
                cameraInstance.cameraControl.startFocusAndMetering(action)
            }
            true
        }
    }

    /**
     * Captures a photo directly to private internal cache storage.
     */
    fun takePhoto(
        onSuccess: (tempFile: File) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val capture = imageCapture ?: run {
            onError(IllegalStateException("Camera not ready"))
            return
        }

        val tempDir = File(context.cacheDir, "tmp").apply { mkdirs() }
        val tempFile = File(tempDir, "photo_${UUID.randomUUID()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        capture.takePicture(
            outputOptions,
            mainExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onSuccess(tempFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }

    /**
     * Starts video recording to a private internal cache file.
     * Enforces visible recording state.
     */
    @SuppressLint("MissingPermission")
    fun startRecording(
        recordAudio: Boolean,
        onVideoSaved: (tempFile: File, durationMs: Long) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val capture = videoCapture ?: run {
            onError(IllegalStateException("Video recorder not ready"))
            return
        }

        if (_recordingState.value.isRecording) return

        val tempDir = File(context.cacheDir, "tmp").apply { mkdirs() }
        val tempFile = File(tempDir, "video_${UUID.randomUUID()}.mp4")
        activeVideoTempFile = tempFile

        val outputOptions = FileOutputOptions.Builder(tempFile).build()

        var pendingRecording = capture.output.prepareRecording(context, outputOptions)

        if (recordAudio) {
            pendingRecording = pendingRecording.withAudioEnabled()
        }

        activeRecording = pendingRecording.start(mainExecutor) { event ->
            when (event) {
                is VideoRecordEvent.Start -> {
                    _recordingState.value = CameraRecordingState(
                        isRecording = true,
                        isPaused = false,
                        durationSeconds = 0L
                    )
                }
                is VideoRecordEvent.Status -> {
                    val durationNanos = event.recordingStats.recordedDurationNanos
                    _recordingState.value = _recordingState.value.copy(
                        durationSeconds = durationNanos / 1_000_000_000L,
                        recordedBytes = event.recordingStats.numBytesRecorded
                    )
                }
                is VideoRecordEvent.Pause -> {
                    _recordingState.value = _recordingState.value.copy(isPaused = true)
                }
                is VideoRecordEvent.Resume -> {
                    _recordingState.value = _recordingState.value.copy(isPaused = false)
                }
                is VideoRecordEvent.Finalize -> {
                    val durationMs = event.recordingStats.recordedDurationNanos / 1_000_000L
                    _recordingState.value = CameraRecordingState()
                    activeRecording = null

                    if (!event.hasError()) {
                        onVideoSaved(tempFile, durationMs)
                    } else {
                        tempFile.delete()
                        onError(event.cause ?: RuntimeException("Video recording failed"))
                    }
                }
            }
        }
    }

    fun pauseRecording() {
        activeRecording?.pause()
    }

    fun resumeRecording() {
        activeRecording?.resume()
    }

    /**
     * Explicit stop action.
     */
    fun stopRecording() {
        activeRecording?.stop()
        activeRecording = null
    }

    /**
     * NON-SURVEILLANCE GUARD:
     * If the app is minimized, screen turned off, or camera screen navigated away from,
     * immediately stop recording.
     */
    fun stopRecordingOnLifecyclePause() {
        if (_recordingState.value.isRecording) {
            stopRecording()
        }
    }
}
