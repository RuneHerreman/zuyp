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
import be.runeherreman.zuyp.domain.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ZuypHangoutOverlay(
    availableUsers: List<User>,
    friends: List<User> = availableUsers,
    addressQuery: String,
    addressSuggestions: List<AddressSuggestion>,
    isAddressLoading: Boolean,
    isAddressSelected: Boolean,
    isSending: Boolean,
    onAddressQueryChange: (String) -> Unit,
    onAddressSelect: (AddressSuggestion) -> Unit,
    onAddressClear: () -> Unit,
    onDismiss: () -> Unit,
    onCreateZuyp: (title: String, start: LocalDateTime, members: List<User>, isPublic: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    val nowRounded = remember {
        LocalDateTime.now().withSecond(0).withNano(0).withMinute(0).plusHours(1)
    }
    var startDateTime by remember { mutableStateOf(nowRounded) }
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
                    endDateTime = null,
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
                        availableUsers = friends,
                        selectedMembers = selectedMembers,
                        memberSearch = memberSearch,
                        onSearchChange = { memberSearch = it },
                        onMemberToggle = { user ->
                            selectedMembers = if (selectedMembers.any { it.id == user.id })
                                selectedMembers.filter { it.id != user.id }
                            else
                                selectedMembers + user
                        },
                        showInviteAll = true,
                        inviteAllUsers = friends,
                        onInviteAll = { invitees ->
                            selectedMembers = invitees.distinctBy { it.id }
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
                val now = remember { LocalDateTime.now() }
                val within24h = startDateTime.isAfter(now) && !startDateTime.isAfter(now.plusHours(24))
                CreateHangoutActions(
                    canCreate = title.isNotBlank() && isAddressSelected && within24h,
                    onCreate = {
                        val finalStart = if (isAllDay)
                            startDateTime.toLocalDate().atStartOfDay()
                        else startDateTime
                        onCreateZuyp(title, finalStart, selectedMembers, isPublic)
                    },
                    onDismiss = onDismiss,
                    createLabel = "ZUYP!",
                    isSending = isSending
                )
            }
        }
    }

    DateTimePickers(
        activePicker = activePicker,
        startDateTime = startDateTime,
        endDateTime = null,
        onStartChange = { startDateTime = it },
        onEndChange = {},
        onDismissPicker = { activePicker = null }
    )
}
