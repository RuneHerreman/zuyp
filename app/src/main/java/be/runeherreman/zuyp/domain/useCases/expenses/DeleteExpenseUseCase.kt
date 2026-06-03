package be.runeherreman.zuyp.domain.useCases.expenses

import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import java.util.UUID
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(expenseId: UUID, requesterId: UUID) {
        expenseRepository.deleteExpense(expenseId, requesterId)
    }
}