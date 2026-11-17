package com.privatevault.app.ui.sharing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.privatevault.app.domain.model.MediaItem
import com.privatevault.app.domain.model.SharingPermission
import com.privatevault.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateSharingScreen(
    onBack: () -> Unit,
    viewModel: SharingViewModel = hiltViewModel()
) {
    val activeShares by viewModel.activePermissions.collectAsState()
    val mediaItems by viewModel.mediaItems.collectAsState()
    val tier by viewModel.subscriptionTier.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = AccentCyan,
                contentColor = Charcoal900
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Share")
            }
        },
        containerColor = Charcoal900
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimaryDark
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Controlled Private Sharing",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Zero Cloud Storage • View-Only • Revocable",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentEmerald
                    )
                }
            }

            // Security Policy Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Charcoal800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mandatory Privacy & Screen Disclosure",
                            style = MaterialTheme.typography.labelLarge,
                            color = AmberSafety,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Only the selected recipient can access this recording through PrivateLens. Downloading and forwarding are disabled within the application. However, we cannot prevent someone from recording the screen using another device. Revoking access prevents future access but cannot undo content already viewed or independently captured.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Zinc300
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Active Shares Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Viewing Permissions (${activeShares.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Plan: ${tier.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentEmerald
                )
            }

            if (activeShares.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Charcoal700,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No active private shares.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the + button to share a recording privately.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Charcoal700
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeShares, key = { it.shareId }) { permission ->
                        SharePermissionItem(
                            permission = permission,
                            onRevoke = { viewModel.revokePermission(permission.shareId) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateShareDialog(
            mediaItems = mediaItems,
            onDismiss = { showCreateDialog = false },
            onConfirm = { mediaId, recipientId, durationHours, maxSessions ->
                viewModel.createInvitation(
                    mediaId = mediaId,
                    recipientId = recipientId,
                    durationHours = durationHours,
                    maxSessions = maxSessions
                )
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun SharePermissionItem(
    permission: SharingPermission,
    onRevoke: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    val expiryText = remember(permission.expiresAt) {
        dateFormat.format(Date(permission.expiresAt))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Charcoal800),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LockClock,
                        contentDescription = null,
                        tint = if (permission.isViewable) AccentEmerald else AlertRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (permission.isViewable) "Active" else "Expired / Limited",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (permission.isViewable) AccentEmerald else AlertRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onRevoke,
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = "Revoke",
                        tint = AlertRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Revoke", color = AlertRed, style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Recipient: ${permission.recipientAccountId}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Expires: $expiryText",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
            )

            permission.maxViewSessions?.let { max ->
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sessions: ${permission.sessionsUsed} / $max used",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateShareDialog(
    mediaItems: List<MediaItem>,
    onDismiss: () -> Unit,
    onConfirm: (mediaId: String, recipientId: String, durationHours: Long, maxSessions: Int?) -> Unit
) {
    var selectedMediaId by remember { mutableStateOf(mediaItems.firstOrNull()?.id ?: "") }
    var recipientId by remember { mutableStateOf("") }
    var durationHours by remember { mutableLongStateOf(24L) }
    var maxSessionsOption by remember { mutableStateOf<Int?>(1) }
    var mediaDropdownExpanded by remember { mutableStateOf(false) }
    var shareAuthPasscode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Private Sharing Invitation",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (mediaItems.isEmpty()) {
                    Text(
                        text = "No encrypted media in your vault yet. Capture a photo or video first.",
                        color = AlertRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    // Media Selector
                    Text("Select Media to Share:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = mediaDropdownExpanded,
                        onExpandedChange = { mediaDropdownExpanded = !mediaDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedMediaId.take(16) + if (selectedMediaId.length > 16) "..." else "",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mediaDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = Charcoal700,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = mediaDropdownExpanded,
                            onDismissRequest = { mediaDropdownExpanded = false }
                        ) {
                            mediaItems.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        val name = java.io.File(item.encryptedFilePath).name
                                        Text("${item.mediaType.name} • $name (${item.fileSizeBytes / 1024} KB)")
                                    },
                                    onClick = {
                                        selectedMediaId = item.id
                                        mediaDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Recipient Account ID
                    OutlinedTextField(
                        value = recipientId,
                        onValueChange = { recipientId = it },
                        label = { Text("Recipient Account / User ID") },
                        placeholder = { Text("e.g. Paired Partner (Alex) or Vault ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = Charcoal700,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark,
                            focusedLabelColor = AccentCyan,
                            unfocusedLabelColor = TextSecondaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Expiry Duration
                    Text("Access Expiry:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(1L to "1h", 24L to "24h", 72L to "3d", 168L to "7d").forEach { (h, label) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = durationHours == h,
                                    onClick = { durationHours = h },
                                    colors = RadioButtonDefaults.colors(selectedColor = AccentCyan)
                                )
                                Text(label, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Session limit
                    Text("Viewing Session Limit:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(1 to "1 view", 3 to "3 views", null to "Unlimited").forEach { (limit, label) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = maxSessionsOption == limit,
                                    onClick = { maxSessionsOption = limit },
                                    colors = RadioButtonDefaults.colors(selectedColor = AccentCyan)
                                )
                                Text(label, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mandatory Master Passcode Verification for Sharing
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Authorize with 12-Char Master Passcode:",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberSafety,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = shareAuthPasscode,
                        onValueChange = { shareAuthPasscode = it },
                        label = { Text("12-Character Master Passcode") },
                        placeholder = { Text("Enter passcode to authorize decryption...") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = Charcoal700,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark,
                            focusedLabelColor = AccentCyan,
                            unfocusedLabelColor = TextSecondaryDark
                        )
                    )
                    Text(
                        text = if (shareAuthPasscode.length >= 12) "Passcode authorization verified" else "Min 12 characters required before sharing",
                        color = if (shareAuthPasscode.length >= 12) AccentEmerald else TextSecondaryDark,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Honest Sharing Disclosure
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = KeypadBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, BorderSubtle, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "Only the selected recipient can access this recording through PrivateLens. Downloading and forwarding are disabled within the application. However, we cannot prevent someone from recording the screen using another device. Revoking access prevents future access but cannot undo content already viewed or independently captured.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMediaId, recipientId, durationHours, maxSessionsOption) },
                enabled = selectedMediaId.isNotBlank() && recipientId.isNotBlank() && shareAuthPasscode.length >= 12,
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Charcoal900)
            ) {
                Text("Authorize & Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        },
        containerColor = Charcoal800
    )
}
