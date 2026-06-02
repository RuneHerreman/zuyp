package be.runeherreman.zuyp.ui.friends.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.User

/**
 * Dialog for editing an existing group: rename it and add/remove members. The
 * group owner is always kept and isn't shown as a removable chip — new members
 * are picked from [friends].
 */
@Composable
fun EditGroupDialog(
    group: Group,
    friends: List<User>,
    onDismiss: () -> Unit,
    onSave: (name: String, members: List<User>) -> Unit
) {
    var name by remember { mutableStateOf(group.name) }
    var memberSearch by remember { mutableStateOf("") }
    // Editable members exclude the owner, who can't be removed from their group.
    var selectedMembers by remember {
        mutableStateOf(group.members.filter { it.id != group.creatorId })
    }

    BackHandler(onBack = onDismiss)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .imePadding()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit group",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FieldLabel("Group name") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Ex. Work group", style = MaterialTheme.typography.bodyMedium) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }

                FieldLabel("Members") {
                    MemberPicker(
                        availableUsers = friends,
                        selectedMembers = selectedMembers,
                        memberSearch = memberSearch,
                        onSearchChange = { memberSearch = it },
                        onMemberToggle = { user ->
                            selectedMembers = if (selectedMembers.any { it.id == user.id })
                                selectedMembers.filter { it.id != user.id }
                            else
                                selectedMembers + user
                        }
                    )
                }

                GroupDialogActions(
                    confirmLabel = "Save",
                    confirmIcon = Icons.Default.Check,
                    confirmEnabled = name.isNotBlank(),
                    onConfirm = { onSave(name, selectedMembers) },
                    onDismiss = onDismiss
                )
            }
        }
    }
}
