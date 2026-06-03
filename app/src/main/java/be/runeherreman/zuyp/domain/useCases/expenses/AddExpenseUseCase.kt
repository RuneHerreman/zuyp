package be.runeherreman.zuyp.domain.useCases.expenses

import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import javax.inject.Inject
import kotlin.math.exp

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        expenseRepository.addExpense(expense)
    }
}