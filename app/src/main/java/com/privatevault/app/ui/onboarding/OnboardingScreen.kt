package com.privatevault.app.ui.onboarding

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.privatevault.app.ui.onboarding.pages.ConsentExplainerPage
import com.privatevault.app.ui.onboarding.pages.EligibilityConfirmPage
import com.privatevault.app.ui.onboarding.pages.LocalStorageExplainerPage
import com.privatevault.app.ui.onboarding.pages.PrivacyPrinciplesPage
import com.privatevault.app.ui.theme.*

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
            .background(TitaniumCanvas)
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
                .background(TitaniumSurfaceGlass)
                .border(1.dp, BorderSubtle)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(state.totalPages) { index ->
                    val isSelected = index == state.currentPage
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (isSelected) 24.dp else 6.dp)
                            .animateContentSize()
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isSelected) TitaniumWhite else BorderSubtle)
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
                            contentColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Back", color = TextSecondary)
                    }
                } else {
                    Spacer(modifier = Modifier.size(1.dp))
                }

                Button(
                    onClick = viewModel::onNextPage,
                    enabled = state.canProceed,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TitaniumWhite,
                        contentColor = TitaniumTextDark,
                        disabledContainerColor = KeypadBg,
                        disabledContentColor = TextDisabled
                    )
                ) {
                    Text(
                        text = if (state.currentPage == state.totalPages - 1) "Enter PrivateLens" else "Continue",
                        fontWeight = FontWeight.Bold,
                        color = if (state.canProceed) TitaniumTextDark else TextDisabled
                    )
                }
            }
        }
    }
}
