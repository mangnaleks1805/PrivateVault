package com.privatevault.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.privatevault.app.ui.onboarding.pages.ConsentExplainerPage
import com.privatevault.app.ui.onboarding.pages.EligibilityConfirmPage
import com.privatevault.app.ui.onboarding.pages.LocalStorageExplainerPage
import com.privatevault.app.ui.onboarding.pages.PrivacyPrinciplesPage
import com.privatevault.app.ui.theme.AccentCyan
import com.privatevault.app.ui.theme.Charcoal600
import com.privatevault.app.ui.theme.Charcoal800
import com.privatevault.app.ui.theme.Charcoal900
import com.privatevault.app.ui.theme.TextPrimaryDark
import com.privatevault.app.ui.theme.TextSecondaryDark

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onOnboardingComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Page Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (state.currentPage) {
                0 -> PrivacyPrinciplesPage(modifier = Modifier.fillMaxSize())
                1 -> EligibilityConfirmPage(
                    isConfirmed = state.is18PlusConfirmed,
                    onConfirmToggled = viewModel::on18PlusToggled,
                    modifier = Modifier.fillMaxSize()
                )
                2 -> ConsentExplainerPage(
                    isAcknowledged = state.isConsentAcknowledged,
                    onAcknowledgeToggled = viewModel::onConsentToggled,
                    modifier = Modifier.fillMaxSize()
                )
                3 -> LocalStorageExplainerPage(
                    isUnderstood = state.isLocalStorageUnderstood,
                    onUnderstoodToggled = viewModel::onLocalStorageToggled,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Charcoal800)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                repeat(state.totalPages) { index ->
                    val isSelected = index == state.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AccentCyan else Charcoal600)
                    )
                }
            }

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.currentPage > 0) {
                    OutlinedButton(
                        onClick = viewModel::onPreviousPage,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondaryDark
                        )
                    ) {
                        Text("Back")
                    }
                } else {
                    Box(modifier = Modifier.size(1.dp))
                }

                Button(
                    onClick = viewModel::onNextPage,
                    enabled = state.canProceed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentCyan,
                        contentColor = Charcoal900,
                        disabledContainerColor = Charcoal600,
                        disabledContentColor = TextSecondaryDark
                    )
                ) {
                    Text(
                        if (state.currentPage == state.totalPages - 1) "Enter Vault" else "Continue"
                    )
                }
            }
        }
    }
}
