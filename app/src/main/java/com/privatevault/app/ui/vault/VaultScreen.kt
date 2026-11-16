package com.privatevault.app.ui.vault

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.privatevault.app.core.security.EncryptionService
import com.privatevault.app.domain.model.MediaItem
import com.privatevault.app.domain.model.MediaType
import com.privatevault.app.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    onBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    viewModel: VaultViewModel = hiltViewModel(),
    encryptionService: EncryptionService
) {
    val uiState by viewModel.uiState.collectAsState()
    val mediaItems by viewModel.mediaItems.collectAsState()
    val folders by viewModel.folders.collectAsState()

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var selectedVaultScope by remember { mutableStateOf("ALL") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            if (uiState.isSelectionMode) {
                // Selection Mode Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Charcoal800)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = viewModel::clearSelection) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = TextPrimaryDark)
                    }
                    Text(
                        text = "${uiState.selectedMediaIds.size} Selected",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentRose)
                    }
                }
            } else {
                // Normal Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                    }
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "Encrypted Vault",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AES-256-GCM Hardware Encrypted",
                                color = AccentEmerald,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.setShowCreateFolderDialog(true) }) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder", tint = AccentCyan)
                    }
                }
            }

            // Storage Overview Card
            val mbUsed = uiState.storageUsageBytes / (1024f * 1024f)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = Charcoal800
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Vault Storage Usage",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f MB (%d items)", mbUsed, mediaItems.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (mbUsed / 500f).coerceIn(0.01f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = AccentCyan,
                        trackColor = Charcoal600
                    )
                }
            }

            // Vault Scope Tabs: [ All Vaults ] | [ Solo Vault ] | [ Duo (Alex) ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = { selectedVaultScope = "ALL" },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedVaultScope == "ALL") AccentCyan else Charcoal800,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "All Vaults",
                            color = if (selectedVaultScope == "ALL") Charcoal900 else TextSecondaryDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    onClick = { selectedVaultScope = "SOLO" },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedVaultScope == "SOLO") TitaniumWhite else Charcoal800,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Solo Vault",
                            color = if (selectedVaultScope == "SOLO") Charcoal900 else TextSecondaryDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    onClick = { selectedVaultScope = "DUO" },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedVaultScope == "DUO") AccentCyan else Charcoal800,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Duo (Alex)",
                            color = if (selectedVaultScope == "DUO") Charcoal900 else TextSecondaryDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Filter Tabs (All / Photos / Videos)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VaultFilterChip(
                    title = "All Media",
                    isSelected = uiState.filter == VaultFilter.ALL && uiState.selectedFolderId == null,
                    onClick = { viewModel.setFilter(VaultFilter.ALL) }
                )
                VaultFilterChip(
                    title = "Photos",
                    isSelected = uiState.filter == VaultFilter.PHOTOS,
                    onClick = { viewModel.setFilter(VaultFilter.PHOTOS) }
                )
                VaultFilterChip(
                    title = "Videos",
                    isSelected = uiState.filter == VaultFilter.VIDEOS,
                    onClick = { viewModel.setFilter(VaultFilter.VIDEOS) }
                )
                folders.forEach { folder ->
                    VaultFilterChip(
                        title = folder.name,
                        isSelected = uiState.selectedFolderId == folder.id,
                        onClick = { viewModel.selectFolder(folder.id) }
                    )
                }
            }

            // Media Grid or Empty State
            if (mediaItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = Charcoal600,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Vault is Empty",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Recordings captured here are encrypted and completely isolated from your device gallery.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        TextButton(onClick = onNavigateToCamera) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = AccentCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Secure Camera", color = AccentCyan)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(mediaItems, key = { it.id }) { item ->
                        MediaThumbnailItem(
                            item = item,
                            isSelected = uiState.selectedMediaIds.contains(item.id),
                            isSelectionMode = uiState.isSelectionMode,
                            onThumbnailRequested = { viewModel.loadThumbnail(item) },
                            onClick = {
                                if (uiState.isSelectionMode) {
                                    viewModel.toggleSelection(item.id)
                                } else {
                                    viewModel.openMedia(item)
                                }
                            },
                            onLongClick = {
                                viewModel.toggleSelection(item.id)
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button (Camera)
        FloatingActionButton(
            onClick = onNavigateToCamera,
            containerColor = AccentCyan,
            contentColor = Charcoal900,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
        }

        // Fullscreen Viewer Dialog
        uiState.selectedMediaItem?.let { selectedItem ->
            MediaViewerDialog(
                item = selectedItem,
                viewModel = viewModel,
                encryptionService = encryptionService,
                onClose = viewModel::closeMedia,
                onDelete = { viewModel.deleteSingleMedia(selectedItem) }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Permanently Delete Media?") },
                text = {
                    Text("Selected recordings and their encryption keys will be securely overwritten with zeros and permanently erased. This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteSelectedMedia()
                            showDeleteConfirmDialog = false
                        }
                    ) {
                        Text("Delete Permanently", color = AccentRose, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Cancel", color = TextPrimaryDark)
                    }
                },
                containerColor = Charcoal800,
                titleContentColor = TextPrimaryDark,
                textContentColor = TextSecondaryDark
            )
        }

        // Create Folder Dialog
        if (uiState.showCreateFolderDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.setShowCreateFolderDialog(false) },
                title = { Text("New Vault Folder") },
                text = {
                    Column {
                        Text("Enter folder name to organize your encrypted media:")
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = newFolderName,
                            onValueChange = { newFolderName = it },
                            placeholder = { Text("e.g. Personal, Trips") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newFolderName.isNotBlank()) {
                                viewModel.createFolder(newFolderName.trim())
                                newFolderName = ""
                            }
                        }
                    ) {
                        Text("Create", color = AccentCyan)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setShowCreateFolderDialog(false) }) {
                        Text("Cancel", color = TextSecondaryDark)
                    }
                },
                containerColor = Charcoal800,
                titleContentColor = TextPrimaryDark,
                textContentColor = TextSecondaryDark
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaThumbnailItem(
    item: MediaItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onThumbnailRequested: suspend () -> Bitmap?,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    var thumbnailBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(item.id) {
        thumbnailBitmap = onThumbnailRequested()
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(Charcoal800)
            .border(
                width = if (isSelected) 3.dp else 0.5.dp,
                color = if (isSelected) AccentCyan else Charcoal700,
                shape = RoundedCornerShape(6.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        if (thumbnailBitmap != null) {
            Image(
                bitmap = thumbnailBitmap!!.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.mediaType == MediaType.VIDEO) Icons.Default.PlayArrow else Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    tint = Charcoal600,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Scope Badge (SOLO vs DUO)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
                .background(Charcoal900.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (item.id.hashCode() % 2 == 0) "SOLO" else "DUO",
                color = if (item.id.hashCode() % 2 == 0) Zinc300 else AccentCyan,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Video Badge
        if (item.mediaType == MediaType.VIDEO) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .background(Charcoal900.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                    item.durationMs?.let { ms ->
                        val sec = (ms / 1000) % 60
                        val min = (ms / 1000) / 60
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format(Locale.US, "%d:%02d", min, sec),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Selection Checkmark
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AccentCyan else Charcoal900.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Charcoal900,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VaultFilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) AccentCyan else Charcoal800,
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = title,
                color = if (isSelected) Charcoal900 else TextSecondaryDark,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
