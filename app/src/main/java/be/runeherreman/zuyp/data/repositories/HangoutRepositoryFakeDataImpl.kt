package be.runeherreman.zuyp.data.repositories

import android.util.Log
import be.runeherreman.zuyp.data.fake.data.FakeDataSource
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
        val hangouts = fakeDataSource.getHangouts().map { dto ->
            Hangout(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                locationName = dto.locationName,
                latitude = dto.latitude,
                longitude = dto.longitude,
                startDate = dto.startDate,
                endDate = dto.endDate,
                attendees = dto.attendees,
                creator = dto.creator,
                private = dto.private
            )
        }
        return flowOf(hangouts)
    }

    override suspend fun getHangoutById(id: UUID): Hangout? {
        return try {
            val dto = fakeDataSource.getHangoutById(id)
            Hangout(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                locationName = dto.locationName,
                latitude = dto.latitude,
                longitude = dto.longitude,
                startDate = dto.startDate,
                endDate = dto.endDate,
                attendees = dto.attendees,
                creator = dto.creator,
                private = dto.private
            )
        } catch (e: NoSuchElementException) {
            Log.e("HangoutError", e.message.toString())
            null
        }
    }

}