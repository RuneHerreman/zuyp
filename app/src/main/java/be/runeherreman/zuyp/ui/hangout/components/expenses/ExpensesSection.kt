package be.runeherreman.zuyp.ui.hangout.components.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.model.PersonBalance
import be.runeherreman.zuyp.ui.friends.components.UserAvatar
import kotlin.math.abs

@Composable
fun ExpensesSection(
    expenses: List<Expense>,
    balances: List<PersonBalance>,
    onAddExpense: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    onSettle: (PersonBalance) -> Unit
) {
    var pendingSettle by remember { mutableStateOf<PersonBalance?>(null) }

    ExpenseHeader(onAddExpense)

    Spacer(modifier = Modifier.height(12.dp))

    if (balances.isNotEmpty()) {
        BalanceSummary(balances = balances, onSettleRequest = { pendingSettle = it })
        Spacer(modifier = Modifier.height(12.dp))
    }

    if (expenses.isEmpty()) {
        EmptyExpensesPlaceHolder()
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            expenses.forEach { expense ->
                ExpenseItem(expense = expense, onClick = { onExpenseClick(expense) })
            }
        }
    }

    pendingSettle?.let { balance ->
        ConfirmMarkAsPaid(
            balance = balance,
            onSettle = onSettle,
            onDismiss = { pendingSettle = null }
        )
    }
}

@Composable
fun ExpenseHeader(onAddExpense: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Expenses", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = onAddExpense),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add expense", tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
fun ConfirmMarkAsPaid(
    balance: PersonBalance,
    onSettle: (PersonBalance) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mark as paid?") },
        text = {
            Text("Settle your € ${"%.2f".format(abs(balance.net))} debt with ${balance.user.name}?")
        },
        confirmButton = {
            TextButton(onClick = { onSettle(balance); onDismiss() }) {
                Text("Mark as paid")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EmptyExpensesPlaceHolder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(16.dp))
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No expenses yet — add the first round!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun BalanceSummary(
    balances: List<PersonBalance>,
    onSettleRequest: (PersonBalance) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "YOUR BALANCE",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        balances.forEach { balance ->
            val theyOweYou = balance.net > 0
            UserBalanceItem(balance, theyOweYou, onSettleRequest)
        }
    }
}

@Composable
fun UserBalanceItem(
    balance: PersonBalance,
    theyOweYou: Boolean,
    onSettleRequest: (PersonBalance) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UserAvatar(user = balance.user, size = 36.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = balance.user.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (theyOweYou) "owes you" else "you owe",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "€ ${"%.2f".format(abs(balance.net))}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (theyOweYou) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        if (!theyOweYou) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onSettleRequest(balance) }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Pay",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun ExpenseItem(expense: Expense, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "paid by ${expense.paidBy.name} • split ${expense.shares.size} ways",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = "€ ${"%.2f".format(expense.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
