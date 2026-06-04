package be.runeherreman.zuyp.domain.repository.sensors

import kotlinx.coroutines.flow.Flow

interface ShakeRepository {
    fun shakes(): Flow<Unit>
}