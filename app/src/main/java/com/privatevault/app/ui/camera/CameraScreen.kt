package com.privatevault.app.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.privatevault.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

@Composable
fun CameraScreen(
    onNavigateToVault: () -> Unit,
    onNavigateToSharing: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onLockNow: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptic = LocalHapticFeedback.current

    val uiState by viewModel.uiState.collectAsState()
    val recordingState by viewModel.recordingState.collectAsState()
    val currentLens by viewModel.currentLens.collectAsState()
    val captureMode by viewModel.captureMode.collectAsState()
    val zoomState by viewModel.zoomState.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    var showFilterCarousel by remember { mutableStateOf(false) }
    var recentSavedPillVisible by remember { mutableStateOf(false) }
    var isShutterPressed by remember { mutableStateOf(false) }
    var vaultDestination by remember { mutableStateOf("SOLO") }
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

    // Show recent saved pill on capture
    LaunchedEffect(uiState.savedMessage) {
        uiState.savedMessage?.let {
            recentSavedPillVisible = true
            delay(3500)
            recentSavedPillVisible = false
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // Shutter scale spring physics
    val shutterScale by animateFloatAsState(
        targetValue = if (isShutterPressed || recordingState.isRecording) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "shutterScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            // ═══════════════════════════════════════════════════════════════
            // SNAPCHAT HORIZONTAL SWIPE GESTURES (Swipe to Vault / Share)
            // ═══════════════════════════════════════════════════════════════
            .pointerInput(Unit) {
                var totalDragX = 0f
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount
                    },
                    onDragEnd = {
                        if (totalDragX > 140f) {
                            // Swipe Left-to-Right -> Opens Vault (Memories)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToVault()
                        } else if (totalDragX < -140f) {
                            // Swipe Right-to-Left -> Opens Controlled Sharing
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToSharing()
                        }
                        totalDragX = 0f
                    },
                    onDragCancel = {
                        totalDragX = 0f
                    }
                )
            }
    ) {
        if (uiState.hasCameraPermission) {
            // Full-Bleed CameraX Viewfinder
            key(currentLens) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            viewModel.bindCamera(lifecycleOwner, this)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Viewfinder Double-Tap Gesture Layer to Flip Camera
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.switchLens()
                            }
                        )
                    }
            )

            // Live Filter Atmospheric Layer (Overlay / Vignette)
            if (currentFilter != PrivacyFilter.ORIGINAL) {
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

                // Active Filter Pill in Viewfinder
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = TitaniumCanvas.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 56.dp)
                        .border(1.dp, BorderHighlight, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✨ ${currentFilter.displayName} (${currentFilter.badge})",
                            color = TitaniumWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
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
                    tint = TitaniumSilver,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Camera Permission Required",
                    style = MaterialTheme.typography.titleLarge,
                    color = TitaniumWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "PrivateLens captures directly to an encrypted local vault. Your media is never shared or stored in public storage.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TitaniumWhite, contentColor = TitaniumTextDark)
                ) {
                    Text("Grant Permission", fontWeight = FontWeight.Bold)
                }
            }
        }

        // ═══════════════════════════════════════════════════════════════════
        // TOP CONTROL BAR (Snapchat Style)
        // ═══════════════════════════════════════════════════════════════════
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Profile & Account Button
            IconButton(
                onClick = onNavigateToAccount,
                enabled = !recordingState.isRecording,
                modifier = Modifier
                    .size(42.dp)
                    .background(TitaniumSurfaceGlass, CircleShape)
                    .border(1.dp, BorderSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile & Account",
                    tint = TitaniumWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Center: Status Badge (Recording or AES-256 Vault)
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
                    color = TitaniumSurfaceGlass,
                    modifier = Modifier.border(1.dp, LeicaRed.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LeicaRed)
                                .alpha(if (recordingState.isPaused) 1f else pulseAlpha)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REC",
                            color = LeicaRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val minutes = recordingState.durationSeconds / 60
                        val seconds = recordingState.durationSeconds % 60
                        Text(
                            text = String.format(Locale.US, "%02d:%02d", minutes, seconds),
                            color = TitaniumWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (recordingState.isPaused) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PAUSED",
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TitaniumSurfaceGlass,
                    modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TitaniumSilver,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AES-256 VAULT",
                            color = TitaniumSilver,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            // Right: Lenses & Beauty Filter Toggle (Camera flipping is done by double-tapping the viewfinder)
            IconButton(
                onClick = { showFilterCarousel = !showFilterCarousel },
                enabled = !recordingState.isRecording,
                modifier = Modifier
                    .size(42.dp)
                    .background(if (showFilterCarousel || currentFilter != PrivacyFilter.ORIGINAL) TitaniumWhite else TitaniumSurfaceGlass, CircleShape)
                    .border(1.dp, BorderSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoFixHigh,
                    contentDescription = "Lenses & Filters",
                    tint = if (showFilterCarousel || currentFilter != PrivacyFilter.ORIGINAL) TitaniumTextDark else TitaniumWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // ═══════════════════════════════════════════════════════════════════
        // FLOATING SNAPCHAT-STYLE SAVED NOTIFICATION TOAST
        // ═══════════════════════════════════════════════════════════════════
        AnimatedVisibility(
            visible = recentSavedPillVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 60 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { 60 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 150.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = TitaniumCanvas.copy(alpha = 0.95f),
                modifier = Modifier
                    .border(1.dp, BorderHighlight, RoundedCornerShape(24.dp))
                    .clickable { onNavigateToVault() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Encrypted to Vault",
                        color = TitaniumWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "VIEW",
                        color = TitaniumSilver,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // ═══════════════════════════════════════════════════════════════════
        // BOTTOM CONTROLS & DOCK (Compact Full-Viewfinder Scrim)
        // ═══════════════════════════════════════════════════════════════════
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.50f)
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(bottom = 6.dp, top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // SNAPCHAT LENS CAROUSEL (Horizontal Strip with Spring Scaling)
            AnimatedVisibility(
                visible = showFilterCarousel && !recordingState.isRecording,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BEAUTY & PRIVACY LENSES",
                            color = TitaniumSilver,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        IconButton(
                            onClick = { showFilterCarousel = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PrivacyFilter.entries.forEach { filter ->
                            val isSelected = currentFilter == filter
                            val lensScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.12f else 0.92f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "lensScale"
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .scale(lensScale)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.setFilter(filter)
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) TitaniumWhite else TitaniumSurfaceGlass)
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) TitaniumWhite else BorderSubtle,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = filter.badge,
                                        color = if (isSelected) TitaniumTextDark else TitaniumWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = filter.displayName,
                                    color = if (isSelected) TitaniumWhite else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Mode Selector (PHOTO vs VIDEO)
            if (!recordingState.isRecording) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(TitaniumSurfaceGlass)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModeTab(
                        title = "PHOTO",
                        selected = captureMode == CaptureMode.PHOTO,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setCaptureMode(CaptureMode.PHOTO)
                        }
                    )
                    ModeTab(
                        title = "VIDEO",
                        selected = captureMode == CaptureMode.VIDEO,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setCaptureMode(CaptureMode.VIDEO)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // ═══════════════════════════════════════════════════════════════
            // VAULT SCOPE TOGGLE: [ Solo Vault ] vs [ Duo (Alex) ]
            // ═══════════════════════════════════════════════════════════════
            if (!recordingState.isRecording) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(TitaniumSurfaceGlass)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (vaultDestination == "SOLO") TitaniumWhite else Color.Transparent)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                vaultDestination = "SOLO"
                            }
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Solo Vault",
                            color = if (vaultDestination == "SOLO") androidx.compose.ui.graphics.Color.Black else TitaniumSilver,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (vaultDestination == "DUO") AccentCyan else Color.Transparent)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                vaultDestination = "DUO"
                            }
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Duo Vault (Alex)",
                            color = if (vaultDestination == "DUO") androidx.compose.ui.graphics.Color.Black else TitaniumSilver,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ═══════════════════════════════════════════════════════════════
            // MAIN SNAPCHAT DOCK (Vault, Shutter with Hold-to-Record, Share)
            // ═══════════════════════════════════════════════════════════════
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT: Encrypted Vault Shortcut
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigateToVault()
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(TitaniumSurfaceGlass)
                            .border(1.2.dp, BorderHighlight, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Encrypted Vault",
                            tint = TitaniumWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "VAULT",
                        color = TitaniumSilver,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                // CENTER: Compact Shutter with Hold-to-Record
                Box(
                    modifier = Modifier
                        .scale(shutterScale)
                        .size(72.dp)
                        .border(
                            width = if (recordingState.isRecording) 3.5.dp else 3.dp,
                            color = if (recordingState.isRecording) LeicaRed else TitaniumSilver,
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(if (recordingState.isRecording || captureMode == CaptureMode.VIDEO) LeicaRed else TitaniumWhite)
                            .pointerInput(recordingState.isRecording, captureMode) {
                                awaitEachGesture {
                                    val down = awaitFirstDown()
                                    isShutterPressed = true

                                    // Wait for either release or long press timeout (300ms)
                                    val upOrCancel = withTimeoutOrNull(300) {
                                        waitForUpOrCancellation()
                                    }

                                    if (upOrCancel != null) {
                                        // Quick Tap (<300ms)
                                        isShutterPressed = false
                                        if (captureMode == CaptureMode.PHOTO) {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.capturePhoto { }
                                        } else {
                                            // In Video mode: tap starts or stops
                                            if (!recordingState.isRecording) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                viewModel.startVideoRecording()
                                            } else {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                viewModel.stopVideoRecording()
                                            }
                                        }
                                    } else {
                                        // Hold (>300ms) -> Snapchat Hold-to-Record Video with Drag-to-Zoom!
                                        val startY = down.position.y
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        if (!recordingState.isRecording) {
                                            viewModel.setCaptureMode(CaptureMode.VIDEO)
                                            viewModel.startVideoRecording()
                                        }

                                        // Snapchat One-Handed Drag-to-Zoom Gesture Loop
                                        var lastHapticStep = 1
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val change = event.changes.firstOrNull() ?: break
                                            if (change.pressed) {
                                                val dragDistanceY = startY - change.position.y
                                                if (dragDistanceY > 20f) {
                                                    val progress = (dragDistanceY / 420f).coerceIn(0f, 1f)
                                                    val targetZoom = 1f + progress * 4f // 1.0x to 5.0x
                                                    viewModel.setZoomRatio(targetZoom)

                                                    val step = targetZoom.toInt()
                                                    if (step > lastHapticStep) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        lastHapticStep = step
                                                    }
                                                } else {
                                                    viewModel.setZoomRatio(1f)
                                                    lastHapticStep = 1
                                                }
                                                change.consume()
                                            } else {
                                                break
                                            }
                                        }

                                        isShutterPressed = false
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        if (recordingState.isRecording) {
                                            viewModel.stopVideoRecording()
                                        }
                                        viewModel.setZoomRatio(1f)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (recordingState.isRecording) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Recording",
                                tint = TitaniumWhite,
                                modifier = Modifier.size(32.dp)
                            )
                        } else if (captureMode == CaptureMode.VIDEO) {
                            Icon(
                                imageVector = Icons.Default.FiberManualRecord,
                                contentDescription = "Video Mode",
                                tint = TitaniumWhite,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                // RIGHT: Friends & Controlled Sharing Shortcut
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigateToSharing()
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(TitaniumSurfaceGlass)
                            .border(1.2.dp, BorderHighlight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Friends & Controlled Sharing",
                            tint = TitaniumWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "FRIENDS",
                        color = TitaniumSilver,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }

        // Encryption in-progress spinner overlay
        AnimatedVisibility(
            visible = uiState.isEncryptingAndSaving,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = TitaniumCanvas.copy(alpha = 0.94f),
                modifier = Modifier.border(1.dp, BorderHighlight, RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = TitaniumWhite, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Encrypting & Storing...",
                        color = TitaniumWhite,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AES-256-GCM hardware keystore encryption",
                        color = TextSecondary,
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
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) TitaniumWhite else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = if (selected) TitaniumTextDark else TextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp
        )
    }
}
