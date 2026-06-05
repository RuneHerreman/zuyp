package be.runeherreman.zuyp.domain.usecases.users

import be.runeherreman.zuyp.domain.repository.sensors.ShakeRepository
import javax.inject.Inject

class StopShakeDetectionUseCase @Inject constructor(
    private val shakeRepository: ShakeRepository
) {
    operator fun invoke() = shakeRepository.stopListening()
}
