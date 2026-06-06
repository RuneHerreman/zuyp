package be.runeherreman.zuyp.fakes

import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.model.PersonBalance
import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

class FakeExpenseRepository : ExpenseRepository {
    private val expenses = MutableStateFlow<List<Expense>>(emptyList())
    val added = mutableListOf<Expense>()

    override suspend fun addExpense(expense: Expense) {
        added += expense
        expenses.update { it + expense }
    }

    override fun getExpenses(hangoutId: UUID): Flow<List<Expense>> = expenses.map { list -> list.filter { it.hangoutId == hangoutId } }

    override suspend fun getExpense(id: UUID): Expense? = expenses.value.find { it.id == id }

    override suspend fun deleteExpense(id: UUID, requesterId: UUID) {
        expenses.update { it.filterNot { e -> e.id == id } }
    }

    override fun getBalances(hangoutId: UUID, forUserId: UUID): Flow<List<PersonBalance>> = flowOf(emptyList())

    override suspend fun settleUp(hangoutId: UUID, fromUserId: UUID, toUserId: UUID, amount: Double) {}
}