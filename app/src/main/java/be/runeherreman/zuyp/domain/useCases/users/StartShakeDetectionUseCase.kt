package be.runeherreman.zuyp.domain.useCases.users

import be.runeherreman.zuyp.domain.repository.sensors.ShakeRepository
import javax.inject.Inject

class StartShakeDetectionUseCase @Inject constructor(
    private val shakeRepository: ShakeRepository
) {
    operator fun invoke() = shakeRepository.startListening()
}
