package be.runeherreman.zuyp.domain.repository

import be.runeherreman.zuyp.domain.model.Hangout
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface HangoutRepository {
    fun getHangouts(): Flow<List<Hangout>>
    fun getAllHangouts(): Flow<List<Hangout>>
    suspend fun getHangoutById(id: UUID): Hangout?
}