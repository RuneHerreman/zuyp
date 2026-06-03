package be.runeherreman.zuyp.ui.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/** Which date/time field the active picker is editing. */
internal enum class PickerTarget { StartDate, StartTime, EndDate, EndTime }

/** The "When is it?" block: all-day toggle plus start/end date & time rows. */
@Composable
internal fun WhenSection(
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime?,
    isAllDay: Boolean,
    dateFormatter: DateTimeFormatter,
    timeFormatter: DateTimeFormatter,
    onAllDayChange: (Boolean) -> Unit,
    onPickerSelect: (PickerTarget) -> Unit
) {
    LabeledField(label = "When is it?") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ToggleRow(
                label = "All day",
                checked = isAllDay,
                onCheckedChange = onAllDayChange
            )
            DateTimeRow(
                label = "Start",
                dateText = startDateTime.format(dateFormatter),
                timeText = startDateTime.format(timeFormatter),
                showTime = !isAllDay,
                onDateClick = { onPickerSelect(PickerTarget.StartDate) },
                onTimeClick = { onPickerSelect(PickerTarget.StartTime) }
            )
            if (endDateTime != null) {
                DateTimeRow(
                    label = "End",
                    dateText = endDateTime.format(dateFormatter),
                    timeText = endDateTime.format(timeFormatter),
                    showTime = !isAllDay,
                    onDateClick = { onPickerSelect(PickerTarget.EndDate) },
                    onTimeClick = { onPickerSelect(PickerTarget.EndTime) }
                )
            }
        }
    }
}

/** Hosts the active date or time picker dialog and reports the picked value. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateTimePickers(
    activePicker: PickerTarget?,
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime?,
    onStartChange: (LocalDateTime) -> Unit,
    onEndChange: (LocalDateTime) -> Unit,
    onDismissPicker: () -> Unit
) {
    when (activePicker) {
        PickerTarget.StartDate, PickerTarget.EndDate -> {
            val isStart = activePicker == PickerTarget.StartDate
            val current = if (isStart) startDateTime else endDateTime ?: run {
                onDismissPicker()
                return
            }
            val pickerState = rememberDatePickerState(
                initialSelectedDateMillis = current.toLocalDate()
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = onDismissPicker,
                confirmButton = {
                    TextButton(onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val pickedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            if (isStart) {
                                val newStart = startDateTime.with(pickedDate)
                                val (s, e) = adjustStart(startDateTime, newStart, endDateTime ?: newStart)
                                onStartChange(s)
                                if (endDateTime != null) onEndChange(e)
                            } else if (endDateTime != null) {
                                onEndChange(adjustEnd(startDateTime, endDateTime.with(pickedDate)))
                            }
                        }
                        onDismissPicker()
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = onDismissPicker) { Text("Cancel") }
                }
            ) {
                DatePicker(state = pickerState)
            }
        }
        PickerTarget.StartTime, PickerTarget.EndTime -> {
            val isStart = activePicker == PickerTarget.StartTime
            val current = if (isStart) startDateTime else endDateTime ?: run {
                onDismissPicker()
                return
            }
            val timeState = rememberTimePickerState(
                initialHour = current.hour,
                initialMinute = current.minute,
                is24Hour = true
            )
            TimePickerDialog(
                onDismiss = onDismissPicker,
                onConfirm = {
                    val pickedTime = current
                        .withHour(timeState.hour)
                        .withMinute(timeState.minute)
                    if (isStart) {
                        val (s, e) = adjustStart(startDateTime, pickedTime, endDateTime ?: pickedTime)
                        onStartChange(s)
                        if (endDateTime != null) onEndChange(e)
                    } else if (endDateTime != null) {
                        onEndChange(adjustEnd(startDateTime, pickedTime))
                    }
                    onDismissPicker()
                }
            ) {
                TimePicker(state = timeState)
            }
        }
        null -> Unit
    }
}

/** Moves the start, keeping the original duration (min 2h) for the end. */
private fun adjustStart(
    oldStart: LocalDateTime,
    newStart: LocalDateTime,
    end: LocalDateTime
): Pair<LocalDateTime, LocalDateTime> {
    val duration = Duration.between(oldStart, end)
    val safeDuration = if (duration.isNegative || duration.isZero) Duration.ofHours(2) else duration
    return newStart to newStart.plus(safeDuration)
}

/** Keeps the end after the start (falls back to start + 1h). */
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
