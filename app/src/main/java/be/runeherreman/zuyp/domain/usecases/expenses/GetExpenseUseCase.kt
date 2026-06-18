package be.runeherreman.zuyp.domain.usecases.expenses

import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import java.util.UUID
import javax.inject.Inject

class GetExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(expenseId: UUID): Expense? {
        return expenseRepository.getExpense(expenseId)
    }
}