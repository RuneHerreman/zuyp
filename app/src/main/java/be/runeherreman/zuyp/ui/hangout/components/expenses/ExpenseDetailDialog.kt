package be.runeherreman.zuyp.ui.hangout.components.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.model.ExpenseShare
import be.runeherreman.zuyp.ui.friends.components.UserAvatar
import coil.compose.AsyncImage
import java.io.File
import java.util.UUID

@Composable
fun ExpenseDetailDialog(
    expense: Expense,
    currentUserId: UUID,
    onDelete: (UUID) -> Unit,
    onDismiss: () -> Unit
) {

    val canDelete = expense.paidBy.id == currentUserId

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
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
                ExpenseDetailHeader(expense, onDismiss)

                Spacer(Modifier.height(8.dp))

                TotalPriceAndImage(expense)

                Spacer(Modifier.height(20.dp))

                Text(
                    "Split (${expense.shares.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                expense.shares.forEachIndexed { index, share ->
                    WhoPaidWhat(share, index, expense, currentUserId)
                }

                Spacer(Modifier.height(20.dp))

                ExpenseDetailFooter(canDelete, onDismiss, onDelete, expense.id)
            }
        }
    }
}

@Composable
fun ExpenseDetailHeader(expense: Expense, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                expense.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Paid by ${expense.paidBy.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }
    }
}

@Composable
fun TotalPriceAndImage(expense: Expense) {
    Text(
        "€ ${"%.2f".format(expense.amount)}",
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    if (expense.imageUri != null) {
        Spacer(Modifier.height(16.dp))
        AsyncImage(
            model = File(expense.imageUri),
            contentDescription = "Bill photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}

@Composable
fun WhoPaidWhat(share: ExpenseShare, index: Int, expense: Expense, currentUserId: UUID) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user = share.user, size = 36.dp)
        Spacer(Modifier.width(12.dp))
        Text(
            text = if (share.user.id == currentUserId) "You" else share.user.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (share.user.id == expense.paidBy.id) {
            Text(
                "paid",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Text(
            "€ ${"%.2f".format(share.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    if (index < expense.shares.size - 1) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
fun ExpenseDetailFooter(canDelete: Boolean, onDismiss: () -> Unit, onDelete: (UUID) -> Unit, expenseId: UUID) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
            Text("Close")
        }
        if (canDelete) {
            Button(
                onClick = { onDelete(expenseId) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Delete")
            }
        }
    }
}