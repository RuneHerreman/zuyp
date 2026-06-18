package be.runeherreman.zuyp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseEntity
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseShareEntity
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseWithDetails
import be.runeherreman.zuyp.data.local.room.entity.expenses.SettlementEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShares(shares: List<ExpenseShareEntity>)

    @Transaction
    suspend fun insertExpenseWithShares(expense: ExpenseEntity, shares: List<ExpenseShareEntity>) {
        insertExpense(expense)
        insertShares(shares)
    }

    @Transaction
    @Query("SELECT * FROM expenses WHERE hangoutId = :hangoutId ORDER BY createdAt DESC")
    fun getExpensesForHangout(hangoutId: UUID): Flow<List<ExpenseWithDetails>>

    @Transaction
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: UUID): ExpenseWithDetails?

    @Transaction
    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: UUID)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementEntity)

    @Query("SELECT * FROM expense_settlements WHERE hangoutId = :hangoutId")
    fun getSettlementsForHangout(hangoutId: UUID): Flow<List<SettlementEntity>>

    @Query("DELETE FROM expenses") suspend fun deleteAllExpenses()
    @Query("DELETE FROM expense_settlements") suspend fun deleteAllSettlements()
}
