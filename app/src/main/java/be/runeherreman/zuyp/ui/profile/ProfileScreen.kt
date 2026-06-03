package be.runeherreman.zuyp.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.ui.profile.components.EditProfileDialog
import be.runeherreman.zuyp.ui.profile.components.ProfileActivitySection
import be.runeherreman.zuyp.ui.profile.components.ProfileHeader
import be.runeherreman.zuyp.ui.profile.components.SettingsDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    onSettingsOpen: () -> Unit = {},
    onSettingsClose: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onEditProfileSave: (String, String, LocalDate) -> Unit = { _, _, _ -> },
    onEditProfileClose: () -> Unit = {},
    onStartupScreenSelect: (String) -> Unit = {},
    onHangoutClick: (Hangout) -> Unit = {},
    onRefresh: () -> Unit = {},
    onFriendsClick: () -> Unit = {},
    onGroupsClick: () -> Unit = {},
    onEventsClick: () -> Unit = {}
) {
    val scheme = MaterialTheme.colorScheme

    // One-shot staggered reveal on first composition.
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(scheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            StaggeredReveal(visible = visible, delayMillis = 0) {
                ProfileHeader(
                    user = uiState.user,
                    friendsCount = uiState.friendsCount,
                    groupsCount = uiState.groupsCount,
                    eventsCount = uiState.eventsCount,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    onSettingsClick = onSettingsOpen,
                    onEditClick = onEditProfile,
                    onFriendsClick = onFriendsClick,
                    onGroupsClick = onGroupsClick,
                    onEventsClick = onEventsClick
                )
            }

            StaggeredReveal(visible = visible, delayMillis = 90) {
                ProfileActivitySection(
                    title = "Owned Activities",
                    icon = Icons.Filled.Star,
                    hangouts = uiState.ownedHangouts,
                    emptyMessage = "You haven't created any activities yet.",
                    accentColor = scheme.primary,
                    accentContainer = scheme.primaryContainer,
                    onAccentContainer = scheme.onPrimaryContainer,
                    onHangoutClick = onHangoutClick
                )
            }

            StaggeredReveal(visible = visible, delayMillis = 180) {
                ProfileActivitySection(
                    title = "Your Upcoming Activities",
                    icon = Icons.Filled.Event,
                    hangouts = uiState.upcomingHangouts,
                    emptyMessage = "No upcoming activities. Time to make plans!",
                    accentColor = scheme.tertiary,
                    accentContainer = scheme.tertiaryContainer,
                    onAccentContainer = scheme.onTertiaryContainer,
                    onHangoutClick = onHangoutClick
                )
            }

            // Breathing room above the bottom navigation bar.
            Column { androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp)) }
        }
    }

    if (uiState.isSettingsOpen) {
        SettingsDialog(
            startupRoute = uiState.startupRoute,
            onEditProfile = onEditProfile,
            onStartupScreenSelect = onStartupScreenSelect,
            onDismiss = onSettingsClose
        )
    }

    if (uiState.isEditProfileOpen) {
        EditProfileDialog(
            user = uiState.user,
            onSave = onEditProfileSave,
            onDismiss = onEditProfileClose
        )
    }
}

@Composable
private fun StaggeredReveal(
    visible: Boolean,
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(durationMillis = 400, delayMillis = delayMillis)) +
                slideInVertically(
                    animationSpec = tween(durationMillis = 400, delayMillis = delayMillis),
                    initialOffsetY = { it / 5 }
                )
    ) {
        content()
    }
}
