package be.runeherreman.zuyp.domain.useCases

import javax.inject.Inject

class GetHangoutsUseCase @Inject constructor(
    private val HangoutRepository: HangoutRepository
) {
}