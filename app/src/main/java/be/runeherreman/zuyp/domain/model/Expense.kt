package be.runeherreman.zuyp.domain.model

import com.android.identity.util.UUID
import java.time.LocalDateTime

data class Expense(
    val id: UUID,
    val hangoutId: UUID,
    val title: String,
    val amount: Double,
    val paidBy: User,
    val imageUri: String?,
    val createdAt: LocalDateTime,
    val shares: List<ExpenseShare>
)

data class ExpenseShare(
    val user: User,
    val amount: Double
)

// Hoeveel een gebruiker moet aan iemand anders
data class PersonBalance(
    val user: User,
    val net: Double
)
