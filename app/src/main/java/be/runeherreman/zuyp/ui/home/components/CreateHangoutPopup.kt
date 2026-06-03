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
import be.runeherreman.zuyp.ui.home.HomeEvent
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
    onEvent: (HomeEvent) -> Unit
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
    var isPrivate by remember { mutableStateOf(true) } // Default to private

    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    BackHandler(onBack = { onEvent(HomeEvent.CreateHangoutClose) })

    Dialog(
        onDismissRequest = { onEvent(HomeEvent.CreateHangoutClose) },
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
                        onQueryChange = { onEvent(HomeEvent.AddressQueryChange(it)) },
                        onSuggestionClick = { onEvent(HomeEvent.AddressSelect(it)) },
                        onClear = { onEvent(HomeEvent.AddressClear) }
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
                        onGroupSelect = { group -> // don't add yourself
                            val members = group.members.filter { it.id != currentUserId }
                            selectedMembers = (selectedMembers + members).distinctBy { it.id }
                            memberSearch = ""
                        }
                    )
                }

                // Private toggle
                ToggleRow(
                    label = "Make this hangout private?",
                    checked = isPrivate,
                    onCheckedChange = { isPrivate = it }
                )

                // Buttons
                CreateHangoutActions(
                    canCreate = title.isNotBlank() && isAddressSelected,
                    onCreate = {
                        val finalStart = if (isAllDay) startDateTime.toLocalDate().atStartOfDay() else startDateTime
                        val finalEnd = if (isAllDay) endDateTime.toLocalDate().atTime(23, 59) else endDateTime

                        onEvent(
                            HomeEvent.CreateHangout(
                                title = title,
                                startDate = finalStart,
                                endDate = finalEnd,
                                users = selectedMembers,
                                private = isPrivate
                            )
                        )
                    },
                    onDismiss = { onEvent(HomeEvent.CreateHangoutClose) }
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
