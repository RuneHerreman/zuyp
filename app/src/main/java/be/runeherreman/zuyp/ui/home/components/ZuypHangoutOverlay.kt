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
import be.runeherreman.zuyp.ui.home.CreateHangoutFormEvent
import be.runeherreman.zuyp.ui.home.HomeEvent
import be.runeherreman.zuyp.ui.home.HomeUiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ZuypHangoutOverlay(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit
) {
    val form = uiState.zuypHangoutForm ?: return
    var activePicker by remember { mutableStateOf<PickerTarget?>(null) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val now = remember { LocalDateTime.now() }

    fun formEvent(e: CreateHangoutFormEvent) = onEvent(HomeEvent.ZuypHangoutFormUpdate(e))

    BackHandler(onBack = { onEvent(HomeEvent.ZuypHangoutClose) })

    Dialog(
        onDismissRequest = { onEvent(HomeEvent.ZuypHangoutClose) },
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

                LabeledField(label = "Hangout name") {
                    OutlinedTextField(
                        value = form.title,
                        onValueChange = { formEvent(CreateHangoutFormEvent.TitleChanged(it)) },
                        placeholder = { Text("Ex. Weekend hang, terrasje", style = MaterialTheme.typography.labelLarge) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.labelLarge
                    )
                }

                WhenSection(
                    startDateTime = form.startDateTime,
                    endDateTime = null,
                    isAllDay = form.isAllDay,
                    dateFormatter = dateFormatter,
                    timeFormatter = timeFormatter,
                    onAllDayChange = { formEvent(CreateHangoutFormEvent.AllDayChanged(it)) },
                    onPickerSelect = { activePicker = it }
                )

                LabeledField(label = "Where is it?") {
                    AddressSelector(
                        query = uiState.addressQuery,
                        suggestions = uiState.addressSuggestions,
                        isLoading = uiState.isAddressLoading,
                        isSelected = uiState.selectedAddress != null,
                        onQueryChange = { onEvent(HomeEvent.AddressQueryChange(it)) },
                        onSuggestionClick = { onEvent(HomeEvent.AddressSelect(it)) },
                        onClear = { onEvent(HomeEvent.AddressClear) }
                    )
                }

                LabeledField(label = "Add members or groups") {
                    MembersSelector(
                        availableUsers = uiState.availableUsers,
                        selectedMembers = form.selectedMembers,
                        memberSearch = form.memberSearch,
                        onSearchChange = { formEvent(CreateHangoutFormEvent.MemberSearchChanged(it)) },
                        onMemberToggle = { formEvent(CreateHangoutFormEvent.MemberToggled(it)) },
                        groups = uiState.availableGroups,
                        onGroupSelect = { formEvent(CreateHangoutFormEvent.GroupSelected(it)) },
                        showInviteAll = true,
                        inviteAllUsers = uiState.availableUsers,
                        onInviteAll = { formEvent(CreateHangoutFormEvent.InviteAll) }
                    )
                }

                ToggleRow(
                    label = "Make this hangout private?",
                    checked = form.isPrivate,
                    onCheckedChange = { formEvent(CreateHangoutFormEvent.PrivateChanged(it)) }
                )

                val within24h = form.startDateTime.isAfter(now) && !form.startDateTime.isAfter(now.plusHours(24))
                CreateHangoutActions(
                    canCreate = form.title.isNotBlank() && uiState.selectedAddress != null && within24h,
                    onCreate = { onEvent(HomeEvent.CreateZuypHangout) },
                    onDismiss = { onEvent(HomeEvent.ZuypHangoutClose) },
                    createLabel = "ZUYP!",
                    isSending = uiState.isZuypSending
                )
            }
        }
    }

    DateTimePickers(
        activePicker = activePicker,
        startDateTime = form.startDateTime,
        endDateTime = null,
        onStartChange = { formEvent(CreateHangoutFormEvent.StartDateChanged(it)) },
        onEndChange = {},
        onDismissPicker = { activePicker = null }
    )
}
