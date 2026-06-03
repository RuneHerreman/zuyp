package be.runeherreman.zuyp.ui.home.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import be.runeherreman.zuyp.domain.model.AddressSuggestion
import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun CreateHangoutPopup(
    availableUsers: List<User>,
    currentUserId: UUID,
    groups: List<Group> = emptyList(),
    addressQuery: String,
    addressSuggestions: List<AddressSuggestion>,
    isAddressLoading: Boolean,
    isAddressSelected: Boolean,
    onAddressQueryChange: (String) -> Unit,
    onAddressSelect: (AddressSuggestion) -> Unit,
    onAddressClear: () -> Unit,
    onDismiss: () -> Unit,
    onCreate: (title: String, start: LocalDateTime, end: LocalDateTime, members: List<User>, isPublic: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    val nowRounded = remember {
        LocalDateTime.now().withSecond(0).withNano(0).withMinute(0).plusHours(1)
    }
    var startDateTime by remember { mutableStateOf(nowRounded) }
    var endDateTime by remember { mutableStateOf(nowRounded.plusHours(2)) }
    var isAllDay by remember { mutableStateOf(false) }
    var activePicker by remember { mutableStateOf<PickerTarget?>(null) }
    var memberSearch by remember { mutableStateOf("") }
    var selectedMembers by remember { mutableStateOf<List<User>>(emptyList()) }
    var isPublic by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

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
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Create a new hangout",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Hangout name
                LabeledField(label = "Hangout name") {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Ex. Weekend hang, terrasje", style = MaterialTheme.typography.labelLarge) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.labelLarge
                    )
                }

                // Date & time
                WhenSection(
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    isAllDay = isAllDay,
                    dateFormatter = dateFormatter,
                    timeFormatter = timeFormatter,
                    onAllDayChange = { isAllDay = it },
                    onPickerSelect = { activePicker = it }
                )

                // Location
                LabeledField(label = "Where is it?") {
                    AddressSelector(
                        query = addressQuery,
                        suggestions = addressSuggestions,
                        isLoading = isAddressLoading,
                        isSelected = isAddressSelected,
                        onQueryChange = onAddressQueryChange,
                        onSuggestionClick = onAddressSelect,
                        onClear = onAddressClear
                    )
                }

                // Members
                LabeledField(label = "Add members or groups") {
                    MembersSelector(
                        availableUsers = availableUsers,
                        selectedMembers = selectedMembers,
                        memberSearch = memberSearch,
                        onSearchChange = { memberSearch = it },
                        onMemberToggle = { user ->
                            selectedMembers = if (selectedMembers.any { it.id == user.id })
                                selectedMembers.filter { it.id != user.id }
                            else
                                selectedMembers + user
                        },
                        groups = groups,
                        onGroupSelect = { group ->
                            // Add the whole group (minus yourself), de-duping people already chosen.
                            val members = group.members.filter { it.id != currentUserId }
                            selectedMembers = (selectedMembers + members).distinctBy { it.id }
                            memberSearch = ""
                        }
                    )
                }

                // Public toggle
                ToggleRow(
                    label = "Set hangout as public?",
                    checked = isPublic,
                    onCheckedChange = { isPublic = it }
                )

                // Buttons
                CreateHangoutActions(
                    canCreate = title.isNotBlank() && isAddressSelected,
                    onCreate = {
                        val finalStart = if (isAllDay)
                            startDateTime.toLocalDate().atStartOfDay()
                        else startDateTime
                        val finalEnd = if (isAllDay)
                            endDateTime.toLocalDate().atTime(23, 59)
                        else endDateTime
                        onCreate(title, finalStart, finalEnd, selectedMembers, isPublic)
                    },
                    onDismiss = onDismiss
                )
            }
        }
    }

    DateTimePickers(
        activePicker = activePicker,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        onStartChange = { startDateTime = it },
        onEndChange = { endDateTime = it },
        onDismissPicker = { activePicker = null }
    )
}
