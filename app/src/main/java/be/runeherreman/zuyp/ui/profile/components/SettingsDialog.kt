package be.runeherreman.zuyp.ui.profile.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SettingsDialog(
    notificationsEnabled: Boolean,
    locationSharingEnabled: Boolean,
    onEditProfile: () -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onLocationSharingToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SettingsDialogHeader(onClose = onDismiss)

                Spacer(modifier = Modifier.size(8.dp))

                NavigationSettingRow(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Update your personal information",
                    onClick = onEditProfile
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ToggleSettingRow(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Event reminders and SOS alerts",
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationsToggle
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ToggleSettingRow(
                    icon = Icons.Default.LocationOn,
                    title = "Location Sharing",
                    subtitle = "Share your location",
                    checked = locationSharingEnabled,
                    onCheckedChange = onLocationSharingToggle
                )
            }
        }
    }
}

@Composable
private fun SettingsDialogHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, contentDescription = "Close settings")
        }
    }
}
