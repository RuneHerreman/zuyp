package be.runeherreman.zuyp.ui.hangout.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import be.runeherreman.zuyp.domain.model.ExpenseShare
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.ui.friends.components.UserAvatar
import be.runeherreman.zuyp.ui.hangout.components.expenses.copyImageIntoAppStorage
import be.runeherreman.zuyp.ui.hangout.components.expenses.expenseImageUri
import be.runeherreman.zuyp.ui.hangout.components.expenses.newExpenseImageFile
import coil.compose.AsyncImage
import java.io.File
import java.util.UUID
import kotlin.math.abs

private enum class SplitMode(val label: String) { EQUALLY("Equally"), CUSTOM("Custom") }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    candidates: List<User>,
    currentUser: User,
    onAdd: (title: String, amount: Double, paidBy: User, shares: List<ExpenseShare>, imageUri: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    var amountText by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var paidBy by remember { mutableStateOf(candidates.firstOrNull { it.id == currentUser.id } ?: candidates.first()) }
    var splitMode by remember { mutableStateOf(SplitMode.EQUALLY) }
    var selectedIds by remember { mutableStateOf(setOf(paidBy.id)) }
    val customAmounts = remember { mutableStateMapOf<UUID, String>() }
    var imagePath by remember { mutableStateOf<String?>(null) }

    // The payer is always part of the split.
    LaunchedEffect(paidBy) { selectedIds = selectedIds + paidBy.id }

    val amount = amountText.replace(',', '.').toDoubleOrNull() ?: 0.0
    val participants = candidates.filter { it.id in selectedIds }
    val shares: List<ExpenseShare> = when (splitMode) {
        SplitMode.EQUALLY -> equalShares(participants, amount, paidBy)
        SplitMode.CUSTOM -> participants.map { ExpenseShare(it, customAmounts[it.id].toAmount()) }
    }
    val customSum = shares.sumOf { it.amount }
    val customOk = splitMode != SplitMode.CUSTOM || abs(customSum - amount) < 0.005
    val canAdd = title.isNotBlank() && amount > 0.0 && participants.isNotEmpty() && customOk

    // Switching to Custom seeds blank fields with the equal split as a starting point.
    LaunchedEffect(splitMode) {
        if (splitMode == SplitMode.CUSTOM && amount > 0.0) {
            equalShares(participants, amount, paidBy).forEach { share ->
                if (customAmounts[share.user.id].isNullOrBlank()) {
                    customAmounts[share.user.id] = "%.2f".format(share.amount)
                }
            }
        }
    }

    // --- bill photo launchers -------------------------------------------------
    var pendingPhoto by remember { mutableStateOf<File?>(null) }
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) imagePath = pendingPhoto?.absolutePath
    }
    fun launchCamera() {
        val file = newExpenseImageFile(context)
        pendingPhoto = file
        takePicture.launch(expenseImageUri(context, file))
    }
    val requestCamera = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera()
    }
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { imagePath = copyImageIntoAppStorage(context, it) }
    }

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

                // Big amount
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("€", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = scheme.onSurface)
                    Spacer(Modifier.width(12.dp))
                    BasicTextField(
                        value = amountText,
                        onValueChange = { new -> amountText = new.filter { it.isDigit() || it == '.' || it == ',' } },
                        textStyle = MaterialTheme.typography.displayMedium.copy(color = scheme.onSurface),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        cursorBrush = SolidColor(scheme.primary),
                        modifier = Modifier.weight(1f),
                        decorationBox = { inner ->
                            if (amountText.isEmpty()) {
                                Text("0.00", style = MaterialTheme.typography.displayMedium, color = scheme.onSurfaceVariant.copy(alpha = 0.5f))
                            }
                            inner()
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))

                FieldLabel("DESCRIPTION")
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("What was it for?") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // Paid by + Split
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        FieldLabel("PAID BY")
                        UserDropdown(
                            selected = paidBy,
                            options = candidates,
                            currentUserId = currentUser.id,
                            onSelect = { paidBy = it }
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        FieldLabel("SPLIT")
                        SplitDropdown(selected = splitMode, onSelect = { splitMode = it })
                    }
                }

                Spacer(Modifier.height(16.dp))

                FieldLabel("WITH WHOM?")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    candidates.forEach { user ->
                        val selected = user.id in selectedIds
                        val isPayer = user.id == paidBy.id
                        FilterChip(
                            selected = selected,
                            onClick = {
                                // payer is always in the split
                                if (!isPayer) {
                                    if (selected) {
                                        selectedIds = selectedIds - user.id
                                    } else {
                                        // In custom mode, seed the new person with what's left.
                                        if (splitMode == SplitMode.CUSTOM && amount > 0.0) {
                                            val used = selectedIds.sumOf { customAmounts[it].toAmount() }
                                            val remaining = (amount - used).coerceAtLeast(0.0)
                                            customAmounts[user.id] = "%.2f".format(remaining)
                                        }
                                        selectedIds = selectedIds + user.id
                                    }
                                }
                            },
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

                // Per-person breakdown
                if (participants.isNotEmpty() && amount > 0.0) {
                    Spacer(Modifier.height(12.dp))
                    if (splitMode == SplitMode.EQUALLY) {
                        shares.forEach { share ->
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
                        participants.forEach { user ->
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
                                    value = customAmounts[user.id] ?: "",
                                    onValueChange = { new -> customAmounts[user.id] = new.filter { it.isDigit() || it == '.' || it == ',' } },
                                    placeholder = { Text("0.00") },
                                    prefix = { Text("€ ") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.width(120.dp)
                                )
                            }
                        }
                        val color = if (customOk) scheme.onSurfaceVariant else scheme.error
                        Text(
                            text = "Split total: € ${"%.2f".format(customSum)} of € ${"%.2f".format(amount)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = color,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Bill photo
                FieldLabel("BILL PHOTO")
                BillPhotoPicker(
                    imagePath = imagePath,
                    onCamera = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            == android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) launchCamera() else requestCamera.launch(Manifest.permission.CAMERA)
                    },
                    onGallery = {
                        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onRemove = { imagePath = null }
                )

                Spacer(Modifier.height(20.dp))

                // Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onAdd(title.trim(), amount, paidBy, shares, imagePath) },
                        enabled = canAdd,
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
private fun UserDropdown(
    selected: User,
    options: List<User>,
    currentUserId: UUID,
    onSelect: (User) -> Unit
) {
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
                DropdownMenuItem(
                    text = { Text(label(user)) },
                    onClick = { onSelect(user); expanded = false }
                )
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
private fun BillPhotoPicker(
    imagePath: String?,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onRemove: () -> Unit
) {
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
private fun PhotoButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit, modifier: Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label)
    }
}

/** Splits [amount] evenly, giving the rounding remainder (cents) to the payer. */
private fun equalShares(participants: List<User>, amount: Double, payer: User): List<ExpenseShare> {
    if (participants.isEmpty()) return emptyList()
    val cents = Math.round(amount * 100)
    val base = cents / participants.size
    val remainder = (cents % participants.size).toInt()
    return participants.map { u ->
        val extra = if (u.id == payer.id) remainder else 0
        ExpenseShare(u, (base + extra) / 100.0)
    }
}

private fun String?.toAmount(): Double = this?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
