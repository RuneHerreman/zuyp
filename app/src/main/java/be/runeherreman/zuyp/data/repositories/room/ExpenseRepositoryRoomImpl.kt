package be.runeherreman.zuyp.data.repositories.room

import be.runeherreman.zuyp.data.local.room.dao.ExpenseDao
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseEntity
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseShareEntity
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseWithDetails
import be.runeherreman.zuyp.data.local.room.entity.expenses.SettlementEntity
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.model.ExpenseShare
import be.runeherreman.zuyp.domain.model.PersonBalance
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.math.abs

class ExpenseRepositoryRoomImpl @Inject constructor(
    private val expenseDao: ExpenseDao
): ExpenseRepository {
    override fun getExpenses(hangoutId: UUID): Flow<List<Expense>> =
        expenseDao.getExpensesForHangout(hangoutId).map { list -> list.map { it.toDomain() } }

    override suspend fun getExpense(id: UUID): Expense? =
        expenseDao.getExpenseById(id)?.toDomain()

    override suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpenseWithShares(
            expense = expense.toEntity(),
            shares = expense.shares.map { ExpenseShareEntity(expense.id, it.user.id, it.amount) }
        )
    }

    override suspend fun deleteExpense(id: UUID, requesterId: UUID) {
        val expense = expenseDao.getExpenseById(id) ?: return
        if (expense.expense.paidByUserId == requesterId) {
            expenseDao.deleteExpenseById(id)
        }
    }

    override fun getBalances(hangoutId: UUID, forUserId: UUID): Flow<List<PersonBalance>> {
        return combine(
            expenseDao.getExpensesForHangout(hangoutId),
            expenseDao.getSettlementsForHangout(hangoutId)
        ) { expenses, settlements ->
            val rawOwed = mutableMapOf<Pair<UUID, UUID>, Double>()
            val users = mutableMapOf<UUID, UserEntity>()

            expenses.forEach { expense ->
                users[expense.payer.id] = expense.payer
                expense.participants.forEach { users[it.id] = it }

                val shareByUser = expense.shares.associate { it.userId to it.shareAmount }

                shareByUser.forEach { (uid, amount) ->
                    if (uid != expense.expense.paidByUserId) {
                        val key = uid to expense.expense.paidByUserId
                        rawOwed[key] = (rawOwed[key] ?: 0.0) + amount
                    }
                }
            }

            val settled = mutableMapOf<Pair<UUID, UUID>, Double>()
            settlements.forEach { settlement ->
                val key = settlement.fromUserId to settlement.toUserId
                settled[key] = (settled[key] ?: 0.0) + settlement.amount
            }

            fun owe(a: UUID, b: UUID) = (rawOwed[a to b] ?: 0.0) - (settled[a to b] ?: 0.0)
            users.keys.filter { it != forUserId }.mapNotNull { other ->
                val net = owe(other, forUserId) - owe(forUserId, other)

                if (abs(net) < 0.005) null
                else PersonBalance(users.getValue(other).toDomain(), net)
            }
        }
    }

    override suspend fun settleUp(
        hangoutId: UUID,
        fromUserId: UUID,
        toUserId: UUID,
        amount: Double
    ) {
        expenseDao.insertSettlement(
            SettlementEntity(UUID.randomUUID(), hangoutId, fromUserId, toUserId, amount, LocalDateTime.now())
        )
    }
}