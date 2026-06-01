package be.runeherreman.zuyp.domain.repository

import be.runeherreman.zuyp.data.local.room.entity.AttendanceStatus
import be.runeherreman.zuyp.domain.model.Hangout
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface HangoutRepository {
    fun getHangouts(): Flow<List<Hangout>>
    fun getAllHangouts(): Flow<List<Hangout>>
    suspend fun getHangoutById(id: UUID): Hangout?
    suspend fun updateAttendenceStatus(hangoutId: UUID, userId: UUID, status: AttendanceStatus)
    suspend fun removeAttendee(hangoutId: UUID, userId: UUID)
    suspend fun createOrUpdateHangout(hangout: Hangout)
    suspend fun removeHangout(hangoutId: UUID)
}