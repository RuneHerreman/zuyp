package be.runeherreman.zuyp.domain.useCases.expenses

import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetHangoutExpensesUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(hangoutId: UUID): Flow<List<Expense>> {
        return expenseRepository.getExpenses(hangoutId)
    }
}
