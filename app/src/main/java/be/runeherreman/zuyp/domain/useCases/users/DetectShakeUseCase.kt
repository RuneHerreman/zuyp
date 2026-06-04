package be.runeherreman.zuyp.domain.useCases.users

import be.runeherreman.zuyp.domain.repository.sensors.ShakeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class DetectShakeUseCase @Inject constructor(
    private val shakeRepository: ShakeRepository
) {
    operator fun invoke(): Flow<Unit> = shakeRepository.shakes()
}