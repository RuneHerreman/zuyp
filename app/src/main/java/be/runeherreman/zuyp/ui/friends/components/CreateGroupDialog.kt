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
import be.runeherreman.zuyp.domain.model.User

/**
 * Dialog for naming a new group and picking its members from the user's friends.
 */
@Composable
fun CreateGroupDialog(
    availableUsers: List<User>,
    onDismiss: () -> Unit,
    onCreate: (name: String, members: List<User>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var memberSearch by remember { mutableStateOf("") }
    var selectedMembers by remember { mutableStateOf<List<User>>(emptyList()) }

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
                    text = "Create a new group",
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

                FieldLabel("Add members") {
                    MemberPicker(
                        availableUsers = availableUsers,
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
                    confirmLabel = "Create",
                    confirmIcon = Icons.Default.Check,
                    confirmEnabled = name.isNotBlank(),
                    onConfirm = { onCreate(name, selectedMembers) },
                    onDismiss = onDismiss
                )
            }
        }
    }
}
