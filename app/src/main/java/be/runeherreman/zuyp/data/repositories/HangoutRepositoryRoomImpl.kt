package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.data.local.room.dao.HangoutDao
import be.runeherreman.zuyp.data.local.room.entity.HangoutWithDetails
import be.runeherreman.zuyp.data.local.room.entity.UserEntity
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class HangoutRepositoryRoomImpl @Inject constructor(
    private val hangoutDao: HangoutDao
): HangoutRepository {

    override fun getHangouts(): Flow<List<Hangout>> {
        return hangoutDao.getAll().map { list -> list.map(HangoutWithDetails::toDomain) }
    }

    override suspend fun getHangoutById(id: UUID): Hangout? {
        return hangoutDao.getById(id)?.toDomain()
    }
}

private fun HangoutWithDetails.toDomain(): Hangout {
    return Hangout(
        id = hangout.id,
        title = hangout.title,
        description = hangout.description,
        locationName = hangout.locationName,
        latitude = hangout.latitude,
        longitude = hangout.longitude,
        startDate = hangout.startDate,
        endDate = hangout.endDate,
        attendees = attendees.map(UserEntity::toDomain),
        creator = creator.toDomain(),
        private = hangout.private
    )
}

private fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        birthdate = birthdate,
        email = email,
        imageUrl = imageUrl
    )
}
