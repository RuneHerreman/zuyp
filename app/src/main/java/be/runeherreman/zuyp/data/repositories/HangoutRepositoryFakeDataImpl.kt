package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.data.fake.data.FakeDataSource
import be.runeherreman.zuyp.data.fake.dto.HangoutDto
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

class HangoutRepositoryFakeDataImpl @Inject constructor(
    private val fakeDataSource: FakeDataSource
) : HangoutRepository {
    override fun getHangouts(): Flow<List<Hangout>> {
        return flowOf(fakeDataSource.getHangouts().map(HangoutDto::toDomain))
    }

    override suspend fun getHangoutById(id: UUID): Hangout? {
        return fakeDataSource.getHangouts().firstOrNull { it.id == id }?.toDomain()
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
