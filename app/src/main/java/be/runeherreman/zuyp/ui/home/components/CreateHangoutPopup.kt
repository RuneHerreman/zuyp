package be.runeherreman.zuyp.ui.home.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import be.runeherreman.zuyp.domain.model.AddressSuggestion
import be.runeherreman.zuyp.domain.model.User
import coil.compose.AsyncImage
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHangoutPopup(
    availableUsers: List<User>,
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
                LabeledField(label = "When is it?") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DateTimeRow(
                            label = "Start",
                            dateText = startDateTime.format(dateFormatter),
                            timeText = startDateTime.format(timeFormatter),
                            showTime = !isAllDay,
                            onDateClick = { activePicker = PickerTarget.StartDate },
                            onTimeClick = { activePicker = PickerTarget.StartTime }
                        )
                        DateTimeRow(
                            label = "End",
                            dateText = endDateTime.format(dateFormatter),
                            timeText = endDateTime.format(timeFormatter),
                            showTime = !isAllDay,
                            onDateClick = { activePicker = PickerTarget.EndDate },
                            onTimeClick = { activePicker = PickerTarget.EndTime }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "All day",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Switch(
                                checked = isAllDay,
                                onCheckedChange = { isAllDay = it }
                            )
                        }
                    }
                }

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
                        }
                    )
                }

                // Public toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Set hangout as public?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val finalStart = if (isAllDay)
                                startDateTime.toLocalDate().atStartOfDay()
                            else startDateTime
                            val finalEnd = if (isAllDay)
                                endDateTime.toLocalDate().atTime(23, 59)
                            else endDateTime
                            onCreate(title, finalStart, finalEnd, selectedMembers, isPublic)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = title.isNotBlank() && isAddressSelected
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Create")
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Close")
                    }
                }
            }
        }
    }

    when (activePicker) {
        PickerTarget.StartDate, PickerTarget.EndDate -> {
            val isStart = activePicker == PickerTarget.StartDate
            val current = if (isStart) startDateTime else endDateTime
            val pickerState = rememberDatePickerState(
                initialSelectedDateMillis = current.toLocalDate()
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { activePicker = null },
                confirmButton = {
                    TextButton(onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val pickedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            if (isStart) {
                                val newStart = startDateTime.with(pickedDate)
                                val (s, e) = adjustStart(startDateTime, newStart, endDateTime)
                                startDateTime = s
                                endDateTime = e
                            } else {
                                endDateTime = adjustEnd(startDateTime, endDateTime.with(pickedDate))
                            }
                        }
                        activePicker = null
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { activePicker = null }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = pickerState)
            }
        }
        PickerTarget.StartTime, PickerTarget.EndTime -> {
            val isStart = activePicker == PickerTarget.StartTime
            val current = if (isStart) startDateTime else endDateTime
            val timeState = rememberTimePickerState(
                initialHour = current.hour,
                initialMinute = current.minute,
                is24Hour = true
            )
            TimePickerDialog(
                onDismiss = { activePicker = null },
                onConfirm = {
                    val pickedTime = current
                        .withHour(timeState.hour)
                        .withMinute(timeState.minute)
                    if (isStart) {
                        val (s, e) = adjustStart(startDateTime, pickedTime, endDateTime)
                        startDateTime = s
                        endDateTime = e
                    } else {
                        endDateTime = adjustEnd(startDateTime, pickedTime)
                    }
                    activePicker = null
                }
            ) {
                TimePicker(state = timeState)
            }
        }
        null -> Unit
    }
}

private enum class PickerTarget { StartDate, StartTime, EndDate, EndTime }

/**
 * Moves the start to [newStart] and shifts the end to preserve the original
 * duration, so dragging the start never produces an end-before-start range.
 */
private fun adjustStart(
    oldStart: LocalDateTime,
    newStart: LocalDateTime,
    end: LocalDateTime
): Pair<LocalDateTime, LocalDateTime> {
    val duration = Duration.between(oldStart, end)
    val safeDuration = if (duration.isNegative || duration.isZero) Duration.ofHours(2) else duration
    return newStart to newStart.plus(safeDuration)
}

/** Clamps [newEnd] so it never lands before [start] (falls back to start + 1h). */
private fun adjustEnd(start: LocalDateTime, newEnd: LocalDateTime): LocalDateTime =
    if (!newEnd.isAfter(start)) start.plusHours(1) else newEnd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimeRow(
    label: String,
    dateText: String,
    timeText: String,
    showTime: Boolean,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(44.dp)
        )
        PickerChip(
            text = dateText,
            icon = Icons.Default.CalendarMonth,
            onClick = onDateClick,
            modifier = Modifier.weight(1f)
        )
        if (showTime) {
            PickerChip(
                text = timeText,
                icon = Icons.Default.Schedule,
                onClick = onTimeClick
            )
        }
    }
}

@Composable
private fun PickerChip(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onConfirm) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    )
}

@Composable
private fun LabeledField(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

@Composable
private fun AddressSelector(
    query: String,
    suggestions: List<AddressSuggestion>,
    isLoading: Boolean,
    isSelected: Boolean,
    onQueryChange: (String) -> Unit,
    onSuggestionClick: (AddressSuggestion) -> Unit,
    onClear: () -> Unit
) {
    // Results only show while the user is searching (not after confirming a pick).
    val showResults = !isSelected && query.isNotBlank() && suggestions.isNotEmpty()
    val searchShape = if (showResults)
        RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
    else
        RoundedCornerShape(14.dp)

    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        // Search bar — turns into a "confirmed" state once a real address is picked.
        Surface(
            shape = searchShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                    shape = searchShape
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.LocationOn else Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                "Ex. Langestraat 28, 8000 Brugge…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                )
                when {
                    isLoading -> CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    isSelected -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Valid address",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    query.isNotEmpty() -> Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp).clickable { onClear() }
                    )
                }
            }
        }

        // Suggestion list — animates in/out below the search bar while typing.
        AnimatedVisibility(
            visible = showResults,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            val resultsShape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)
            Surface(
                shape = resultsShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, resultsShape)
            ) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    suggestions.forEachIndexed { index, suggestion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSuggestionClick(suggestion) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = suggestion.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (suggestion.fullAddress != suggestion.name) {
                                    Text(
                                        text = suggestion.fullAddress,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        if (index < suggestions.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 46.dp, end = 14.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MembersSelector(
    availableUsers: List<User>,
    selectedMembers: List<User>,
    memberSearch: String,
    onSearchChange: (String) -> Unit,
    onMemberToggle: (User) -> Unit
) {
    val filtered = availableUsers
        .filter { user ->
            selectedMembers.none { it.id == user.id } &&
                    memberSearch.isNotBlank() &&
                    user.name.contains(memberSearch, ignoreCase = true)
        }
        .take(5)

    val showResults = memberSearch.isNotEmpty() && filtered.isNotEmpty()
    // Keep the results dropdown above the keyboard: scroll it into view once it
    // appears (and whenever the result count changes its height).
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    LaunchedEffect(showResults, filtered.size) {
        if (showResults) bringIntoViewRequester.bringIntoView()
    }
    val searchShape = if (showResults)
        RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
    else
        RoundedCornerShape(14.dp)

    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        // Search bar — shape morphs when results appear
        Surface(
            shape = searchShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, searchShape)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                BasicTextField(
                    value = memberSearch,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (memberSearch.isEmpty()) {
                            Text(
                                "Search people…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                )
                if (memberSearch.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp).clickable { onSearchChange("") }
                    )
                }
            }
        }

        // Results animate in/out below — only appears while typing
        AnimatedVisibility(
            visible = showResults,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            val resultsShape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)
            Surface(
                shape = resultsShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, resultsShape)
            ) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    filtered.forEachIndexed { index, user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMemberToggle(user); onSearchChange("") }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .border(1.5.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (user.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = user.imageUrl,
                                        contentDescription = user.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.name.first().uppercaseChar().toString(),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        if (index < filtered.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 62.dp, end = 14.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }

        // Selected chips — shown below the search/results block
        if (selectedMembers.isNotEmpty()) {
            Spacer(Modifier.size(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(selectedMembers) { user ->
                    InputChip(
                        selected = true,
                        onClick = { onMemberToggle(user) },
                        label = { Text(user.name, style = MaterialTheme.typography.labelMedium) },
                        avatar = {
                            if (user.imageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = user.imageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(InputChipDefaults.AvatarSize).clip(CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(InputChipDefaults.AvatarSize)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onPrimaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.name.first().uppercaseChar().toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        trailingIcon = {
                            Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                        },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
    }
}
