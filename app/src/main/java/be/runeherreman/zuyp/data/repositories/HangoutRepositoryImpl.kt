package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.data.fake.data.FakeDataSource
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class HangoutRepositoryImpl @Inject constructor(
    private val fakeDataSource: FakeDataSource
): HangoutRepository {
    override fun getHangouts(): Flow<List<Hangout>> {
        return fakeDataSource.getHangouts().map { dto ->
            Hangout(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                location = dto.location,
                date = dto.date,
                attendees = dto.attendees,
                creator = dto.creator,
                private = dto.private
            )
        } as Flow<List<Hangout>>
    }

    override suspend fun getHangoutById(id: UUID): Hangout? {
        return fakeDataSource.getHangoutById(id)?.let { dto ->
            Hangout(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                location = dto.location,
                date = dto.date,
                attendees = dto.attendees,
                creator = dto.creator,
                private = dto.private
            )
        }
    }
}