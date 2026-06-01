package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.data.fake.data.FakeDataSource
import be.runeherreman.zuyp.data.fake.dto.HangoutDto
import be.runeherreman.zuyp.data.local.room.entity.AttendanceStatus
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class HangoutRepositoryFakeDataImpl @Inject constructor(
    private val fakeDataSource: FakeDataSource
) : HangoutRepository {
    override fun getAllHangouts(): Flow<List<Hangout>> {
        return flowOf(
            fakeDataSource.getHangouts()
                .map(HangoutDto::toDomain)
                .sortedBy { it.startDate }
        )
    }

    override fun getHangouts(): Flow<List<Hangout>> {
        val now = LocalDateTime.now()
        return flowOf(
            fakeDataSource.getHangouts()
                .map(HangoutDto::toDomain)
                .filter { it.startDate.isAfter(now) }
                .sortedBy { it.startDate }
        )
    }

    override suspend fun getHangoutById(id: UUID): Hangout? {
        return fakeDataSource.getHangouts().firstOrNull { it.id == id }?.toDomain()
    }

    override suspend fun updateAttendenceStatus(
        hangoutId: UUID,
        userId: UUID,
        status: AttendanceStatus
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun removeAttendee(hangoutId: UUID, userId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun createOrUpdateHangout(hangout: Hangout) {
        TODO("Not yet implemented")
    }

    override suspend fun removeHangout(hangoutId: UUID) {
        TODO("Not yet implemented")
    }
}

private fun HangoutDto.toDomain() = Hangout(
    id = id,
    title = title,
    description = description,
    locationName = locationName,
    latitude = latitude,
    longitude = longitude,
    startDate = startDate,
    endDate = endDate,
    attendees = attendees,
    creator = creator,
    private = private
)
