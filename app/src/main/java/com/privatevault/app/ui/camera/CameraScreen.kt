package com.privatevault.app.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.privatevault.app.core.camera.CaptureMode
import com.privatevault.app.core.camera.PrivacyFilter
import com.privatevault.app.core.camera.PrivacyFilterProcessor
import com.privatevault.app.ui.theme.AccentCyan
import com.privatevault.app.ui.theme.AccentEmerald
import com.privatevault.app.ui.theme.AccentRose
import com.privatevault.app.ui.theme.Charcoal800
import com.privatevault.app.ui.theme.Charcoal900
import com.privatevault.app.ui.theme.TextPrimaryDark
import com.privatevault.app.ui.theme.TextSecondaryDark
import java.util.Locale

@Composable
fun CameraScreen(
    onBack: () -> Unit,
    onMediaSaved: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.uiState.collectAsState()
    val recordingState by viewModel.recordingState.collectAsState()
    val currentLens by viewModel.currentLens.collectAsState()
    val captureMode by viewModel.captureMode.collectAsState()
    val zoomState by viewModel.zoomState.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    var showFilterTray by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Multi-permission launcher (Camera + Microphone)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA]
            ?: (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO]
            ?: (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)

        viewModel.onPermissionsResult(cameraGranted, audioGranted)
    }

    // Check permissions on start
    LaunchedEffect(Unit) {
        val hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val hasAudio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (!hasCamera) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        } else {
            viewModel.onPermissionsResult(hasCamera, hasAudio)
        }
    }

    // Strict non-surveillance rule: Stop recording on lifecycle pause
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                viewModel.cameraManager.stopRecordingOnLifecyclePause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.cameraManager.stopRecordingOnLifecyclePause()
        }
    }

    // Snackbar notifications
    LaunchedEffect(uiState.savedMessage, uiState.errorMessage) {
        uiState.savedMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (uiState.hasCameraPermission) {
            // CameraX Viewfinder with pinch-to-zoom & tap-to-focus
            key(currentLens) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            viewModel.bindCamera(lifecycleOwner, this)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (currentFilter != PrivacyFilter.ORIGINAL) {
                // Live Viewfinder Vignette Overlay for Golden Hour, 90s Film, Noir & Teal-Orange
                if (currentFilter in listOf(PrivacyFilter.GOLDEN_HOUR, PrivacyFilter.VINTAGE_90S, PrivacyFilter.VOGUE_NOIR, PrivacyFilter.CYBER_MOODY)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.radialGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.52f))
                                )
                            )
                    )
                }

                // Live Subtle Ambient Atmosphere (Overlay blend mode preserves blacks and highlights)
                val ambientMood = when (currentFilter) {
                    PrivacyFilter.GOLDEN_HOUR -> Color(0x35FFA000)
                    PrivacyFilter.BLUSH_PEACH -> Color(0x28FF8A80)
                    PrivacyFilter.VOGUE_NOIR -> Color(0x45212121)
                    PrivacyFilter.CYBER_MOODY -> Color(0x2800ACC1)
                    PrivacyFilter.ETHEREAL_DREAM -> Color(0x25FFF59D)
                    else -> Color.Transparent
                }
                if (ambientMood != Color.Transparent) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(color = ambientMood, blendMode = BlendMode.Overlay)
                    }
                }

                // Active Filter Watermark Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Charcoal900.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 58.dp, end = 16.dp)
                        .border(1.dp, AccentCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AccentCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${currentFilter.displayName} • ${currentFilter.badge}",
                            color = AccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        } else {
            // Permission Rationale Card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Camera Permission Required",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Private Vault captures directly to an encrypted local vault. Your media is never shared or stored in public storage.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Charcoal900)
                ) {
                    Text("Grant Permission")
                }
            }
        }

        // Top Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                enabled = !recordingState.isRecording
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Visible Recording Indicator & Timer
            if (recordingState.isRecording) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseAlpha"
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Charcoal900.copy(alpha = 0.85f),
                    modifier = Modifier.border(1.dp, AccentRose, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(AccentRose)
                                .alpha(if (recordingState.isPaused) 1f else pulseAlpha)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val minutes = recordingState.durationSeconds / 60
                        val seconds = recordingState.durationSeconds % 60
                        Text(
                            text = String.format(Locale.US, "%02d:%02d", minutes, seconds),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (recordingState.isPaused) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PAUSED",
                                color = AccentRose,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Encryption badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Charcoal900.copy(alpha = 0.7f),
                        modifier = Modifier.border(1.dp, AccentEmerald.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AES-256 VAULT", color = AccentEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Privacy Filter Toggle Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (showFilterTray) AccentCyan else Charcoal900.copy(alpha = 0.7f),
                        modifier = Modifier
                            .border(1.dp, if (showFilterTray) AccentCyan else TextSecondaryDark.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .clickable { showFilterTray = !showFilterTray }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = "Privacy Filters",
                                tint = if (showFilterTray) Charcoal900 else AccentCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentFilter.badge,
                                color = if (showFilterTray) Charcoal900 else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Audio Toggle (only applies in video mode)
            if (captureMode == CaptureMode.VIDEO) {
                IconButton(
                    onClick = viewModel::toggleAudio,
                    enabled = !recordingState.isRecording
                ) {
                    Icon(
                        imageVector = if (uiState.isAudioEnabled && uiState.hasAudioPermission) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Audio Toggle",
                        tint = if (uiState.isAudioEnabled) AccentCyan else Color.Gray
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        // Bottom Controls Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Charcoal900.copy(alpha = 0.75f))
                .navigationBarsPadding()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Privacy Filter Tray (Horizontal Scrollable Strip)
            AnimatedVisibility(
                visible = showFilterTray && !recordingState.isRecording,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrivacyFilter.entries.forEach { filter ->
                        val isSelected = currentFilter == filter
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) AccentCyan else Charcoal800,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.setFilter(filter) }
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = filter.badge,
                                    color = if (isSelected) Charcoal900 else AccentEmerald,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = filter.displayName,
                                    color = if (isSelected) Charcoal900 else TextPrimaryDark,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                Text(
                                    text = filter.category,
                                    color = if (isSelected) Charcoal900.copy(alpha = 0.7f) else TextSecondaryDark,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Zoom indicator & quick zoom pills (Pinch to zoom also active)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Charcoal800)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(1f, 2f).forEach { targetZoom ->
                    val isSelected = (zoomState.zoomRatio - targetZoom).let { it >= -0.15f && it <= 0.15f }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) AccentCyan else Color.Transparent)
                            .clickable { viewModel.setZoomRatio(targetZoom) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${targetZoom.toInt()}x",
                            color = if (isSelected) Charcoal900 else TextSecondaryDark,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }

                if (zoomState.zoomRatio > 1.15f && (zoomState.zoomRatio - 2f).let { it < -0.15f || it > 0.15f }) {
                    Text(
                        text = String.format(Locale.US, "%.1fx", zoomState.zoomRatio),
                        color = AccentEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mode Selector (Photo vs Video)
            if (!recordingState.isRecording) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Charcoal800)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModeTab(
                        title = "PHOTO",
                        selected = captureMode == CaptureMode.PHOTO,
                        onClick = { viewModel.setCaptureMode(CaptureMode.PHOTO) }
                    )
                    ModeTab(
                        title = "VIDEO",
                        selected = captureMode == CaptureMode.VIDEO,
                        onClick = { viewModel.setCaptureMode(CaptureMode.VIDEO) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Shutter & Actions Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left slot: Lens Switcher (disabled during video recording)
                if (!recordingState.isRecording) {
                    IconButton(
                        onClick = viewModel::switchLens,
                        modifier = Modifier
                            .size(52.dp)
                            .background(Charcoal800, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Switch Camera",
                            tint = Color.White
                        )
                    }
                } else {
                    // While recording: Pause/Resume button
                    IconButton(
                        onClick = {
                            if (recordingState.isPaused) {
                                viewModel.resumeVideoRecording()
                            } else {
                                viewModel.pauseVideoRecording()
                            }
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .background(Charcoal800, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (recordingState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause/Resume",
                            tint = Color.White
                        )
                    }
                }

                // Center slot: Big Shutter / Record Button
                if (captureMode == CaptureMode.PHOTO) {
                    // Photo Shutter
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .border(4.dp, Color.White, CircleShape)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable(enabled = !uiState.isEncryptingAndSaving) {
                                viewModel.capturePhoto {
                                    onMediaSaved()
                                }
                            }
                    )
                } else {
                    // Video Shutter / Stop
                    if (!recordingState.isRecording) {
                        // Start Record Button
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .border(4.dp, Color.White, CircleShape)
                                .padding(6.dp)
                                .clip(CircleShape)
                                .background(AccentRose)
                                .clickable(enabled = !uiState.isEncryptingAndSaving) {
                                    viewModel.startVideoRecording()
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FiberManualRecord,
                                contentDescription = "Start Recording",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    } else {
                        // Stop Record Button
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .border(4.dp, AccentRose, CircleShape)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentRose)
                                .clickable {
                                    viewModel.stopVideoRecording()
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop Recording",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }

                // Right slot: Placeholder for balance
                Spacer(modifier = Modifier.size(52.dp))
            }
        }

        // Encryption in-progress spinner overlay
        AnimatedVisibility(
            visible = uiState.isEncryptingAndSaving,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Charcoal900.copy(alpha = 0.9f),
                modifier = Modifier.border(1.dp, AccentCyan, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = AccentCyan, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Encrypting & Storing...",
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AES-256-GCM hardware encryption",
                        color = TextSecondaryDark,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 96.dp)
        )
    }
}

@Composable
private fun ModeTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) AccentCyan else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = if (selected) Charcoal900 else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
