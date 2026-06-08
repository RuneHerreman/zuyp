package be.runeherreman.zuyp.fakes

import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class FakeHangoutRepository : HangoutRepository {

    private val hangouts = MutableStateFlow<List<Hangout>>(emptyList())

    val updateCalls = mutableListOf<Triple<UUID, UUID, AttendanceStatus>>()
    val removeCalls = mutableListOf<Pair<UUID, UUID>>()
    val createdHangouts = mutableListOf<Hangout>()
    val removedHangoutIds = mutableListOf<UUID>()

    override fun getHangouts(): Flow<List<Hangout>> = hangouts
    override fun getAllHangouts(): Flow<List<Hangout>> = hangouts

    override suspend fun getHangoutById(id: UUID): Hangout? =
        hangouts.value.find { it.id == id }

    override suspend fun updateAttendenceStatus(
        hangoutId: UUID,
        userId: UUID,
        status: AttendanceStatus,
    ) {
        updateCalls += Triple(hangoutId, userId, status)
    }

    override suspend fun removeAttendee(hangoutId: UUID, userId: UUID) {
        removeCalls += Pair(hangoutId, userId)
    }

    override suspend fun createOrUpdateHangout(hangout: Hangout) {
        createdHangouts += hangout
        hangouts.value = hangouts.value + hangout
    }

    override suspend fun removeHangout(hangoutId: UUID) {
        removedHangoutIds += hangoutId
        hangouts.value = hangouts.value.filterNot { it.id == hangoutId }
    }
}
