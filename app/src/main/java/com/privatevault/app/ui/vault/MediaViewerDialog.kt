package com.privatevault.app.ui.vault

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.privatevault.app.core.playback.EncryptedMediaDataSource
import com.privatevault.app.core.security.EncryptionService
import com.privatevault.app.domain.model.MediaItem
import com.privatevault.app.domain.model.MediaType
import com.privatevault.app.ui.theme.AccentCyan
import com.privatevault.app.ui.theme.AccentRose
import com.privatevault.app.ui.theme.Charcoal900
import com.privatevault.app.ui.theme.TextPrimaryDark

@Composable
fun MediaViewerDialog(
    item: MediaItem,
    viewModel: VaultViewModel,
    encryptionService: EncryptionService,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (item.mediaType == MediaType.PHOTO) {
                // Photo Viewer
                var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
                var scale by remember { mutableFloatStateOf(1f) }
                var offsetX by remember { mutableFloatStateOf(0f) }
                var offsetY by remember { mutableFloatStateOf(0f) }

                LaunchedEffect(item.id) {
                    photoBitmap = viewModel.loadFullPhoto(item)
                }

                if (photoBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale = (scale * zoom).coerceIn(1f, 4f)
                                    if (scale > 1f) {
                                        offsetX += pan.x
                                        offsetY += pan.y
                                    } else {
                                        offsetX = 0f
                                        offsetY = 0f
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = photoBitmap!!.asImageBitmap(),
                            contentDescription = "Decrypted Photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offsetX,
                                    translationY = offsetY
                                )
                        )
                    }
                } else {
                    CircularProgressIndicator(
                        color = AccentCyan,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                // Video Player with AndroidX Media3 ExoPlayer
                var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
                var isReady by remember { mutableStateOf(false) }

                LaunchedEffect(item.id) {
                    val dek = viewModel.getMediaDEK(item)
                    val encryptedFile = viewModel.getEncryptedFile(item)

                    if (dek != null && encryptedFile.exists()) {
                        val dataSourceFactory = EncryptedMediaDataSource.Factory(
                            encryptedFile = encryptedFile,
                            dek = dek,
                            encryptionService = encryptionService
                        )
                        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(ExoMediaItem.fromUri("vault://media/${item.id}"))

                        val player = ExoPlayer.Builder(context).build().apply {
                            setMediaSource(mediaSource)
                            prepare()
                            playWhenReady = true
                        }
                        exoPlayer = player
                        isReady = true
                    }
                }

                DisposableEffect(Unit) {
                    onDispose {
                        exoPlayer?.release()
                        exoPlayer = null
                    }
                }

                if (isReady && exoPlayer != null) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = true
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CircularProgressIndicator(
                        color = AccentCyan,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Top Bar Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.background(Charcoal900.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.background(Charcoal900.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentRose)
                }
            }
        }
    }
}
