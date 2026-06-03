package be.runeherreman.zuyp.domain.repository

import android.content.pm.LauncherUserInfo
import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.model.PersonBalance
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ExpenseRepository {
    fun getExpenses(hangoutId: UUID): Flow<List<Expense>>
    suspend fun getExpense(id: UUID): Expense?
    suspend fun addExpense(expense: Expense)
    suspend fun deleteExpense(id: UUID, requesterId: UUID)
    fun getBalances(hangoutId: UUID, forUserId: UUID): Flow<List<PersonBalance>>
    suspend fun settleUp(hangoutId: UUID, fromUserId: UUID, toUserid: UUID, amount: Double)
}