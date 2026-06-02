package be.runeherreman.zuyp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.ui.profile.components.ProfileActivitySection
import be.runeherreman.zuyp.ui.profile.components.ProfileHeader
import be.runeherreman.zuyp.ui.profile.components.SettingsDialog

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    onSettingsOpen: () -> Unit = {},
    onSettingsClose: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onPrivacySettings: () -> Unit = {},
    onNotificationsToggle: (Boolean) -> Unit = {},
    onLocationSharingToggle: (Boolean) -> Unit = {},
    onHangoutClick: (Hangout) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        ProfileHeader(
            user = uiState.user,
            friendsCount = uiState.friendsCount,
            groupsCount = uiState.groupsCount,
            eventsCount = uiState.eventsCount,
            modifier = Modifier.padding(top = 16.dp),
            onSettingsClick = onSettingsOpen
        )

        ProfileActivitySection(
            title = "Owned Activities",
            hangouts = uiState.ownedHangouts,
            emptyMessage = "You haven't created any activities yet.",
            onHangoutClick = onHangoutClick
        )

        ProfileActivitySection(
            title = "Your Upcoming Activities",
            hangouts = uiState.upcomingHangouts,
            emptyMessage = "No upcoming activities. Time to make plans!",
            onHangoutClick = onHangoutClick,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    if (uiState.isSettingsOpen) {
        SettingsDialog(
            notificationsEnabled = uiState.notificationsEnabled,
            locationSharingEnabled = uiState.locationSharingEnabled,
            onEditProfile = onEditProfile,
            onPrivacySettings = onPrivacySettings,
            onNotificationsToggle = onNotificationsToggle,
            onLocationSharingToggle = onLocationSharingToggle,
            onDismiss = onSettingsClose
        )
    }
}
