package be.runeherreman.zuyp.domain.useCases.expenses

import be.runeherreman.zuyp.domain.model.PersonBalance
import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetEventBalancesUseCase @Inject constructor(
    private val eventRepository: ExpenseRepository
){
    suspend operator fun invoke(hangoutId: UUID, forUserId: UUID): Flow<List<PersonBalance>> {
        return eventRepository.getBalances(hangoutId, forUserId)
    }
}