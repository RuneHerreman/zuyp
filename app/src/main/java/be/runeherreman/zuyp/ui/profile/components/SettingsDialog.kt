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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import be.runeherreman.zuyp.ui.navigation.Screen
import be.runeherreman.zuyp.ui.navigation.screens

@Composable
fun SettingsDialog(
    startupRoute: String?,
    onEditProfile: () -> Unit,
    onStartupScreenSelect: (String) -> Unit,
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

                LaunchPageSection(
                    selectedRoute = startupRoute ?: Screen.Home.route,
                    onSelect = onStartupScreenSelect
                )
            }
        }
    }
}

/** Lets the user pick which screen the app opens on launch. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaunchPageSection(
    selectedRoute: String,
    onSelect: (String) -> Unit
) {
    val selectedScreen = screens.firstOrNull { it.route == selectedRoute } ?: screens.first()
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = "Launch page",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 14.dp, bottom = 8.dp)
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = stringResource(selectedScreen.labelResourceId),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Open on launch") },
            leadingIcon = {
                Icon(imageVector = selectedScreen.icon, contentDescription = null)
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            screens.forEach { screen ->
                DropdownMenuItem(
                    text = { Text(stringResource(screen.labelResourceId)) },
                    leadingIcon = {
                        Icon(imageVector = screen.icon, contentDescription = null)
                    },
                    onClick = {
                        onSelect(screen.route)
                        expanded = false
                    }
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
