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
    onTitleChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onPaidByChanged: (UUID) -> Unit,
    onSplitModeChanged: (SplitMode) -> Unit,
    onParticipantToggled: (UUID) -> Unit,
    onCustomAmountChanged: (UUID, String) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onRemoveImage: () -> Unit,
    onAdd: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = scheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "New expense",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = scheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = scheme.error)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Centered amount input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("€", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = scheme.onSurface)
                    Spacer(Modifier.width(12.dp))
                    BasicTextField(
                        value = form.amountText,
                        onValueChange = { new -> onAmountChanged(new.filter { it.isDigit() || it == '.' || it == ',' }) },
                        textStyle = MaterialTheme.typography.displayMedium.copy(
                            color = scheme.onSurface,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        cursorBrush = SolidColor(scheme.primary),
                        modifier = Modifier.widthIn(min = 120.dp),
                        decorationBox = { inner ->
                            Box(contentAlignment = Alignment.Center) {
                                if (form.amountText.isEmpty()) {
                                    Text(
                                        "0.00",
                                        style = MaterialTheme.typography.displayMedium,
                                        color = scheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                inner()
                            }
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))

                FieldLabel("DESCRIPTION")
                OutlinedTextField(
                    value = form.title,
                    onValueChange = onTitleChanged,
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
                            onSelect = { onPaidByChanged(it.id) }
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        FieldLabel("SPLIT")
                        SplitDropdown(selected = form.splitMode, onSelect = onSplitModeChanged)
                    }
                }

                Spacer(Modifier.height(16.dp))

                FieldLabel("WITH WHOM?")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    form.candidates.forEach { user ->
                        val selected = user.id in form.selectedParticipantIds
                        FilterChip(
                            selected = selected,
                            onClick = { onParticipantToggled(user.id) },
                            label = { Text(if (user.id == currentUser.id) "You" else user.name.substringBefore(' ')) },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = scheme.primary,
                                selectedLabelColor = scheme.onPrimary,
                                selectedLeadingIconColor = scheme.onPrimary
                            )
                        )
                    }
                }

                if (form.shares.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    if (form.splitMode == SplitMode.EQUALLY) {
                        form.shares.forEach { share ->
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
                                    color = scheme.onSurface
                                )
                                Text(
                                    "€ ${"%.2f".format(share.amount)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = scheme.onSurface
                                )
                            }
                        }
                    } else {
                        val amount = form.amountText.replace(',', '.').toDoubleOrNull() ?: 0.0
                        form.participants.forEach { user ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                UserAvatar(user = user, size = 28.dp)
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    if (user.id == currentUser.id) "You" else user.name,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = scheme.onSurface
                                )
                                OutlinedTextField(
                                    value = form.customAmounts[user.id] ?: "",
                                    onValueChange = { new -> onCustomAmountChanged(user.id, new.filter { it.isDigit() || it == '.' || it == ',' }) },
                                    placeholder = { Text("0.00") },
                                    prefix = { Text("€ ") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.width(120.dp)
                                )
                            }
                        }
                        Text(
                            text = "Split total: € ${"%.2f".format(form.customSum)} of € ${"%.2f".format(amount)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (form.customOk) scheme.onSurfaceVariant else scheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                FieldLabel("BILL PHOTO")
                BillPhotoPicker(
                    imagePath = form.imagePath,
                    onCamera = onCameraClick,
                    onGallery = onGalleryClick,
                    onRemove = onRemoveImage
                )

                Spacer(Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onAdd,
                        enabled = form.canAdd,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add expense")
                    }
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Cancel")
                    }
                }
            }
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
    val scheme = MaterialTheme.colorScheme
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
                    .background(scheme.scrim.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = scheme.inverseOnSurface)
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
