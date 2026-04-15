package be.runeherreman.zuyp.domain.useCases

import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import java.util.UUID
import javax.inject.Inject

class GetHangoutByIdUseCase @Inject constructor(
    private val hangoutRepository: HangoutRepository
) {
    suspend operator fun invoke(id: String): Hangout? {
        return hangoutRepository.getHangoutById(UUID.fromString(id))
    }
}