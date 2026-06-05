package be.runeherreman.zuyp.domain.utils

import be.runeherreman.zuyp.domain.model.ExpenseShare
import be.runeherreman.zuyp.domain.model.User
import java.util.UUID
import javax.inject.Inject

class ExpenseSplitter @Inject constructor() {

    // Split equally across participants, leftover cents are for payer
    fun equalShares(participants: List<User>, amount: Double, payer: User): List<ExpenseShare> {
        if (participants.isEmpty()) return emptyList()
        val cents = Math.round(amount * 100)
        val base = cents / participants.size
        val remainder = (cents % participants.size).toInt()
        return participants.map { user ->
            val userCents = base + if (user.id == payer.id) remainder else 0
            ExpenseShare(user, userCents / 100.0)
        }
    }

    fun customShares(participants: List<User>, customAmounts: Map<UUID, String>): List<ExpenseShare> =
        participants.map { ExpenseShare(it, customAmounts[it.id].toAmount()) }

    // When adding new person or changing amounts, it will distribute the remaining or subtract the overflow from other users
    fun autoFillAmounts(
        participants: List<User>,
        amount: Double,
        lockedIds: Set<UUID>,
        customAmounts: Map<UUID, String>,
    ): Map<UUID, String> {
        if (participants.isEmpty()) return customAmounts

        val lockedSum = participants
            .filter { it.id in lockedIds }
            .sumOf { customAmounts[it.id].toAmount() }

        val unlocked = participants.filter { it.id !in lockedIds }
        val autoAmount = if (unlocked.isNotEmpty()) (amount - lockedSum) / unlocked.size else 0.0

        return customAmounts + unlocked.associate {
            it.id to "%.2f".format(autoAmount.coerceAtLeast(0.0))
        }
    }

    fun sharesMatchTotal(shares: List<ExpenseShare>, amount: Double): Boolean =
        kotlin.math.abs(shares.sumOf { it.amount } - amount) < 0.005 // Within 0.5 cents

    private fun String?.toAmount(): Double =
        this?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
}
