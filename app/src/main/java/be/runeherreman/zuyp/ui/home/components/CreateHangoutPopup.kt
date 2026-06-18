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
import be.runeherreman.zuyp.ui.home.CreateHangoutForm
import be.runeherreman.zuyp.ui.home.CreateHangoutFormEvent
import be.runeherreman.zuyp.ui.home.HomeEvent
import be.runeherreman.zuyp.ui.home.HomeUiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CreateHangoutPopup(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit
) {
    val form = uiState.createHangoutForm ?: return
    HangoutFormDialog(
        form = form,
        uiState = uiState,
        title = "Create a new hangout",
        showEndDate = true,
        showInviteAll = false,
        canCreate = form.title.isNotBlank() && uiState.selectedAddress != null,
        onFormEvent = { onEvent(HomeEvent.CreateHangoutFormUpdate(it)) },
        onHomeEvent = onEvent,
        createEvent = HomeEvent.CreateHangout,
        dismissEvent = HomeEvent.CreateHangoutClose
    )
}

@Composable
fun ZuypHangoutOverlay(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit
) {
    val form = uiState.zuypHangoutForm ?: return
    val now = remember { LocalDateTime.now() }
    val within24h = form.startDateTime.isAfter(now) && !form.startDateTime.isAfter(now.plusHours(24))
    HangoutFormDialog(
        form = form,
        uiState = uiState,
        title = "Create a new hangout",
        showEndDate = false,
        showInviteAll = true,
        showAllDay = false,
        canCreate = form.title.isNotBlank() && uiState.selectedAddress != null && within24h,
        createLabel = "ZUYP!",
        isSending = uiState.isZuypSending,
        onRightNowClick = {
            onEvent(HomeEvent.ZuypHangoutFormUpdate(
                CreateHangoutFormEvent.StartDateChanged(LocalDateTime.now().plusMinutes(2))
            ))
        },
        onFormEvent = { onEvent(HomeEvent.ZuypHangoutFormUpdate(it)) },
        onHomeEvent = onEvent,
        createEvent = HomeEvent.CreateZuypHangout,
        dismissEvent = HomeEvent.ZuypHangoutClose
    )
}

@Composable
private fun HangoutFormDialog(
    form: CreateHangoutForm,
    uiState: HomeUiState,
    title: String,
    showEndDate: Boolean,
    showInviteAll: Boolean,
    canCreate: Boolean,
    showAllDay: Boolean = true,
    createLabel: String = "Create",
    isSending: Boolean = false,
    onRightNowClick: () -> Unit = {},
    onFormEvent: (CreateHangoutFormEvent) -> Unit,
    onHomeEvent: (HomeEvent) -> Unit,
    createEvent: HomeEvent,
    dismissEvent: HomeEvent
) {
    var activePicker by remember { mutableStateOf<PickerTarget?>(null) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    BackHandler(onBack = { onHomeEvent(dismissEvent) })

    Dialog(
        onDismissRequest = { onHomeEvent(dismissEvent) },
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
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                LabeledField(label = "Hangout name") {
                    OutlinedTextField(
                        value = form.title,
                        onValueChange = { onFormEvent(CreateHangoutFormEvent.TitleChanged(it)) },
                        placeholder = { Text("Ex. Weekend hang, terrasje", style = MaterialTheme.typography.labelLarge) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.labelLarge
                    )
                }

                WhenSection(
                    startDateTime = form.startDateTime,
                    endDateTime = if (showEndDate) form.endDateTime else null,
                    isAllDay = form.isAllDay,
                    dateFormatter = dateFormatter,
                    timeFormatter = timeFormatter,
                    onAllDayChange = { onFormEvent(CreateHangoutFormEvent.AllDayChanged(it)) },
                    onPickerSelect = { activePicker = it },
                    showAllDay = showAllDay,
                    onAsapClick = onRightNowClick
                )

                LabeledField(label = "Where is it?") {
                    AddressSelector(
                        query = uiState.addressQuery,
                        suggestions = uiState.addressSuggestions,
                        isLoading = uiState.isAddressLoading,
                        isSelected = uiState.selectedAddress != null,
                        onQueryChange = { onHomeEvent(HomeEvent.AddressQueryChange(it)) },
                        onSuggestionClick = { onHomeEvent(HomeEvent.AddressSelect(it)) },
                        onClear = { onHomeEvent(HomeEvent.AddressClear) }
                    )
                }

                LabeledField(label = "Add members or groups") {
                    MembersSelector(
                        availableUsers = uiState.availableUsers,
                        selectedMembers = form.selectedMembers,
                        memberSearch = form.memberSearch,
                        onSearchChange = { onFormEvent(CreateHangoutFormEvent.MemberSearchChanged(it)) },
                        onMemberToggle = { onFormEvent(CreateHangoutFormEvent.MemberToggled(it)) },
                        groups = uiState.availableGroups,
                        onGroupSelect = { onFormEvent(CreateHangoutFormEvent.GroupSelected(it)) },
                        showInviteAll = showInviteAll,
                        onInviteAll = if (showInviteAll) { { onFormEvent(CreateHangoutFormEvent.InviteAll) } } else null
                    )
                }

                ToggleRow(
                    label = "Make this hangout private?",
                    checked = form.isPrivate,
                    onCheckedChange = { onFormEvent(CreateHangoutFormEvent.PrivateChanged(it)) }
                )

                CreateHangoutActions(
                    canCreate = canCreate,
                    createEvent = createEvent,
                    dismissEvent = dismissEvent,
                    onEvent = onHomeEvent,
                    createLabel = createLabel,
                    isSending = isSending
                )
            }
        }
    }

    DateTimePickers(
        activePicker = activePicker,
        startDateTime = form.startDateTime,
        endDateTime = if (showEndDate) form.endDateTime else null,
        onStartChange = { onFormEvent(CreateHangoutFormEvent.StartDateChanged(it)) },
        onEndChange = { if (showEndDate) onFormEvent(CreateHangoutFormEvent.EndDateChanged(it)) },
        onDismissPicker = { activePicker = null }
    )
}
