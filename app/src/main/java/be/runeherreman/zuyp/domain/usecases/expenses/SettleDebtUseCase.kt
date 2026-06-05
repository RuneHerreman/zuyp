package be.runeherreman.zuyp.domain.usecases.expenses

import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import java.util.UUID
import javax.inject.Inject

class SettleDebtUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(hangoutId: UUID, from: UUID, to: UUID, amount: Double) {
        expenseRepository.settleUp(hangoutId, from, to, amount)
    }
}