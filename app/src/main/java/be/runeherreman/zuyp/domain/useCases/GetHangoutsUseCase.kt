package be.runeherreman.zuyp.domain.useCases

import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHangoutsUseCase @Inject constructor(
    private val hangoutRepository: HangoutRepository
) {
    operator fun invoke(): Flow<List<Hangout>> {
        return hangoutRepository.getHangouts()
    }
}