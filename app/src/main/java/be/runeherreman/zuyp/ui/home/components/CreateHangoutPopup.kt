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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import be.runeherreman.zuyp.domain.model.User
import coil.compose.AsyncImage
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHangoutPopup(
    availableFriends: List<User>,
    onDismiss: () -> Unit,
    onCreate: (title: String, date: LocalDate, location: String, members: List<User>, isPublic: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var location by remember { mutableStateOf("") }
    var memberSearch by remember { mutableStateOf("") }
    var selectedMembers by remember { mutableStateOf<List<User>>(emptyList()) }
    var isPublic by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM/dd/yyyy") }

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

                // Date
                LabeledField(label = "When is it?") {
                    OutlinedTextField(
                        value = selectedDate.format(dateFormatter),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.labelLarge,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Pick date"
                                )
                            }
                        }
                    )
                }

                // Location
                LabeledField(label = "Where is it?") {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("Ex. Langestraat 28, 8000 Brugge, België", style = MaterialTheme.typography.labelLarge) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.labelLarge
                    )
                }

                // Members
                LabeledField(label = "Add members or groups") {
                    MembersSelector(
                        availableFriends = availableFriends,
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
                        onClick = { onCreate(title, selectedDate, location, selectedMembers, isPublic) },
                        modifier = Modifier.weight(1f),
                        enabled = title.isNotBlank()
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

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
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
private fun MembersSelector(
    availableFriends: List<User>,
    selectedMembers: List<User>,
    memberSearch: String,
    onSearchChange: (String) -> Unit,
    onMemberToggle: (User) -> Unit
) {
    val filtered = availableFriends
        .filter { friend ->
            selectedMembers.none { it.id == friend.id } &&
                    memberSearch.isNotBlank() &&
                    friend.name.contains(memberSearch, ignoreCase = true)
        }
        .take(5)

    val showResults = memberSearch.isNotEmpty() && filtered.isNotEmpty()
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
