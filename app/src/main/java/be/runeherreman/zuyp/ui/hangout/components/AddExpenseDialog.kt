package be.runeherreman.zuyp.ui.hangout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import be.runeherreman.zuyp.domain.model.ExpenseShare
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.ui.friends.components.UserAvatar
import be.runeherreman.zuyp.ui.hangout.AddExpenseEvent
import be.runeherreman.zuyp.ui.hangout.AddExpenseForm
import be.runeherreman.zuyp.ui.hangout.SplitMode
import coil.compose.AsyncImage
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    form: AddExpenseForm,
    currentUser: User,
    onEvent: (AddExpenseEvent) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
) {
    Dialog(onDismissRequest = { onEvent(AddExpenseEvent.Dismiss) }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                AddExpenseHeader(onEvent = onEvent)

                Spacer(Modifier.height(8.dp))

                AmountInput(form, onEvent)

                Spacer(Modifier.height(20.dp))

                FieldLabel("DESCRIPTION")
                OutlinedTextField(
                    value = form.title,
                    onValueChange = { onEvent(AddExpenseEvent.TitleChanged(it)) },
                    placeholder = { Text("What was it for?") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        FieldLabel("PAID BY")
                        UserDropdown(
                            selected = form.paidBy ?: currentUser,
                            options = form.candidates,
                            currentUserId = currentUser.id,
                            onSelect = { onEvent(AddExpenseEvent.PaidByChanged(it.id)) }
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        FieldLabel("SPLIT")
                        SplitDropdown(selected = form.splitMode, onSelect = { onEvent(AddExpenseEvent.SplitModeChanged(it)) })
                    }
                }

                Spacer(Modifier.height(16.dp))

                UserListPicker(form, currentUser, onEvent)

                if (form.shares.isNotEmpty()) {
                    ExpenseShares(form, currentUser, onEvent)
                }

                Spacer(Modifier.height(16.dp))

                FieldLabel("BILL PHOTO")
                BillPhotoPicker(
                    imagePath = form.imagePath,
                    onCamera = onCameraClick,
                    onGallery = onGalleryClick,
                    onRemove = { onEvent(AddExpenseEvent.ImageRemoved) }
                )

                Spacer(Modifier.height(20.dp))

                AddExpenseFooter(form, onEvent)
            }
        }
    }
}

@Composable
fun UserListPicker(form: AddExpenseForm, currentUser: User, onEvent: (AddExpenseEvent) -> Unit) {
    FieldLabel("WITH WHOM?")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        form.candidates.forEach { user ->
            val selected = user.id in form.selectedParticipantIds
            FilterChip(
                selected = selected,
                onClick = { onEvent(AddExpenseEvent.ParticipantToggled(user.id)) },
                label = { Text(if (user.id == currentUser.id) "You" else user.name.substringBefore(' ')) },
                leadingIcon = if (selected) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun ExpenseShares(form: AddExpenseForm, currentUser: User, onEvent: (AddExpenseEvent) -> Unit) {
    Spacer(Modifier.height(12.dp))
    if (form.splitMode == SplitMode.EQUALLY) {
        form.shares.forEach { share ->
            EqualSplitRow(share, currentUser)
        }
    } else {
        val amount = form.amountText.replace(',', '.').toDoubleOrNull() ?: 0.0
        val payer = form.paidBy
        val payerIsCurrentUser = payer?.id == currentUser.id
        val payerLabel = if (payerIsCurrentUser) "you" else payer?.name ?: "the payer"
        Text(
            text = "Each amount is what that person owes $payerLabel. ${if (payerIsCurrentUser) "Your own share is what you absorb." else "${payer?.name}'s share is what they absorb."}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        form.participants.forEach { user ->
            CustomSplitRow(currentUser, user, form, payer, onEvent)
        }
        Text(
            text = "Split total: € ${"%.2f".format(form.customSum)} of € ${"%.2f".format(amount)}",
            style = MaterialTheme.typography.labelMedium,
            color = if (form.customOk) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun AddExpenseFooter(form: AddExpenseForm, onEvent: (AddExpenseEvent) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { onEvent(AddExpenseEvent.Submit) },
            enabled = form.canAdd,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Add")
        }
        OutlinedButton(onClick = { onEvent(AddExpenseEvent.Dismiss) }, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Cancel")
        }
    }
}

@Composable
fun CustomSplitRow(
    currentUser: User,
    user: User,
    form: AddExpenseForm,
    payer: User?,
    onEvent: (AddExpenseEvent) -> Unit
) {
    val isPayer  = user.id == payer?.id
    val isLocked = user.id in form.lockedParticipantIds
    val sublabel = when {
        isPayer && user.id == currentUser.id -> "you absorb"
        isPayer -> "absorbs"
        user.id == currentUser.id -> "you owe ${payer?.name ?: "payer"}"
        else -> "owes ${if (payer?.id == currentUser.id) "you" else payer?.name ?: "payer"}"
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user = user, size = 28.dp)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                if (user.id == currentUser.id) "You" else user.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                if (isLocked) sublabel else "auto · $sublabel",
                style = MaterialTheme.typography.labelSmall,
                color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.primary
            )
        }
        OutlinedTextField(
            value = form.customAmounts[user.id] ?: "",
            onValueChange = { new -> onEvent(AddExpenseEvent.CustomAmountChanged(user.id, new.filter { it.isDigit() || it == '.' || it == ',' })) },
            placeholder = { Text("0.00") },
            prefix = { Text("€ ") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.width(120.dp)
        )
    }
}

@Composable
fun EqualSplitRow(share: ExpenseShare, currentUser: User) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user = share.user, size = 28.dp)
        Spacer(Modifier.width(10.dp))
        Text(
            if (share.user.id == currentUser.id) "You" else share.user.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "€ ${"%.2f".format(share.amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AmountInput(form: AddExpenseForm, onEvent: (AddExpenseEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("€", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.width(12.dp))
        BasicTextField(
            value = form.amountText,
            onValueChange = { new -> onEvent(AddExpenseEvent.AmountChanged(new.filter { it.isDigit() || it == '.' || it == ',' })) },
            textStyle = MaterialTheme.typography.displayMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.widthIn(min = 120.dp),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.Center) {
                    if (form.amountText.isEmpty()) {
                        Text(
                            "0.00",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                    inner()
                }
            }
        )
    }
}

@Composable
fun AddExpenseHeader(onEvent: (AddExpenseEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "New expense",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = { onEvent(AddExpenseEvent.Dismiss) }) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserDropdown(selected: User, options: List<User>, currentUserId: UUID, onSelect: (User) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    fun label(u: User) = if (u.id == currentUserId) "You" else u.name
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = label(selected),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { user ->
                DropdownMenuItem(text = { Text(label(user)) }, onClick = { onSelect(user); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SplitDropdown(selected: SplitMode, onSelect: (SplitMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SplitMode.entries.forEach { mode ->
                DropdownMenuItem(text = { Text(mode.label) }, onClick = { onSelect(mode); expanded = false })
            }
        }
    }
}

@Composable
private fun BillPhotoPicker(imagePath: String?, onCamera: () -> Unit, onGallery: () -> Unit, onRemove: () -> Unit) {
    if (imagePath != null) {
        Box(modifier = Modifier.fillMaxWidth().heightIn(max = 180.dp)) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = "Bill photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(14.dp))
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = MaterialTheme.colorScheme.inverseOnSurface)
            }
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            PhotoButton(Icons.Default.CameraAlt, "Camera", onCamera, Modifier.weight(1f))
            PhotoButton(Icons.Default.PhotoLibrary, "Gallery", onGallery, Modifier.weight(1f))
        }
    }
}

@Composable
private fun PhotoButton(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label)
    }
}
