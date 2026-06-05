package be.runeherreman.zuyp.data.repositories.room

import be.runeherreman.zuyp.data.local.room.dao.HangoutDao
import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutUsersMapping
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutWithDetails
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class HangoutRepositoryRoomImpl @Inject constructor(
    private val hangoutDao: HangoutDao
): HangoutRepository {

    override fun getAllHangouts(): Flow<List<Hangout>> {
        return hangoutDao.getAll().map { list ->
            list.map(HangoutWithDetails::toDomain).sortedBy { it.startDate }
        }
    }

    override fun getHangouts(): Flow<List<Hangout>> {
        return hangoutDao.getAll().map { list ->
            val now = LocalDateTime.now()
            list.map(HangoutWithDetails::toDomain)
                .filter { it.endDate.isAfter(now) }
                .sortedBy { it.startDate }
        }
    }

    override suspend fun getHangoutById(id: UUID): Hangout? {
        return hangoutDao.getById(id)?.toDomain()
    }

    override suspend fun updateAttendenceStatus(
        hangoutId: UUID,
        userId: UUID,
        status: AttendanceStatus
    ) {
        hangoutDao.insertOrUpdateAttendee(HangoutUsersMapping(hangoutId, userId, status))
    }

    override suspend fun removeAttendee(hangoutId: UUID, userId: UUID) {
        hangoutDao.removeAttendee(hangoutId, userId)
    }

    override suspend fun createOrUpdateHangout(hangout: Hangout) {
        val hangoutEntity = hangout.toEntity()

        val mappingEntities = hangout.attendees.map { user ->
            HangoutUsersMapping(
                hangoutId = hangout.id,
                userId = user.id,
                status = user.attendanceStatus ?: AttendanceStatus.GOING
            )
        }

        hangoutDao.insertHangoutWithAttendees(hangoutEntity, mappingEntities)
    }

    override suspend fun removeHangout(hangoutId: UUID) {
        hangoutDao.removeHangout(hangoutId)
    }
}

