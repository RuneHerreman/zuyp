package be.runeherreman.zuyp.domain.useCases

import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import javax.inject.Inject

class CreateHangoutUseCase @Inject constructor(
    private val hangoutRepository: HangoutRepository
) {
    suspend operator fun invoke(hangout: Hangout) {
        hangoutRepository.createOrUpdateHangout(hangout)
    }
}
