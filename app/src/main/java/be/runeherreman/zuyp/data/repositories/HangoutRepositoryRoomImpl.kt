package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.data.local.room.dao.HangoutDao
import be.runeherreman.zuyp.data.local.room.entity.AttendanceStatus
import be.runeherreman.zuyp.data.local.room.entity.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.HangoutUsersMapping
import be.runeherreman.zuyp.data.local.room.entity.HangoutWithDetails
import be.runeherreman.zuyp.data.local.room.entity.UserEntity
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
                .filter { it.startDate.isAfter(now) }
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
        hangoutDao.insert(hangout.toEntity())
    }
}

private fun Hangout.toEntity(): HangoutEntity {
    return HangoutEntity(
        id = id,
        title = title,
        description = description,
        locationName = locationName,
        latitude = latitude,
        longitude = longitude,
        startDate = startDate,
        endDate = endDate,
        creatorId = creator.id,
        private = private
    )
}

private fun HangoutWithDetails.toDomain(): Hangout {
    val statusMap = attendanceStatuses.associateBy { it.userId }
    return Hangout(
        id = hangout.id,
        title = hangout.title,
        description = hangout.description,
        locationName = hangout.locationName,
        latitude = hangout.latitude,
        longitude = hangout.longitude,
        startDate = hangout.startDate,
        endDate = hangout.endDate,
        attendees = attendees.map { userEntity ->
            userEntity.toDomain(statusMap[userEntity.id]?.status)
        },
        creator = creator.toDomain(),
        private = hangout.private
    )
}

private fun UserEntity.toDomain(attendanceStatus: AttendanceStatus? = null): User {
    return User(
        id = id,
        name = name,
        birthdate = birthdate,
        email = email,
        imageUrl = imageUrl,
        attendanceStatus = attendanceStatus?.let {
            when (it) {
                AttendanceStatus.GOING -> AttendanceStatus.GOING
                AttendanceStatus.NOT_INTERESTED -> AttendanceStatus.NOT_INTERESTED
            }
        }
    )
}
