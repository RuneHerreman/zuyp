package be.runeherreman.zuyp.domain.usecases.expenses

import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        expenseRepository.addExpense(expense)
    }
}