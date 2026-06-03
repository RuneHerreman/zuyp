package be.runeherreman.zuyp.data.local.room.database

import androidx.room.withTransaction
import be.runeherreman.zuyp.data.fake.data.FakeDataSource
import be.runeherreman.zuyp.data.local.room.dao.HangoutDao
import be.runeherreman.zuyp.data.local.room.dao.UserDao
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutUsersMapping
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val database: AppDatabase,
    private val hangoutDao: HangoutDao,
    private val userDao: UserDao,
    private val fakeDataSource: FakeDataSource
) {
    suspend fun seedIfNeeded() {
        if (hangoutDao.countHangouts() > 0) return

        val fakeHangouts = fakeDataSource.getHangouts()
        val usersByEmail = linkedMapOf<String, UserEntity>()
        fakeHangouts
            .flatMap { hangout -> listOf(hangout.creator) + hangout.attendees }
            .forEach { user ->
                val emailKey = user.email.normalizedEmailKey()
                if (emailKey !in usersByEmail) {
                    usersByEmail[emailKey] = UserEntity(
                        id = user.id,
                        name = user.name,
                        birthdate = user.birthdate,
                        email = user.email.trim(),
                        imageUrl = user.imageUrl
                    )
                }
            }

        val users = usersByEmail.values.toList()
        val userIdByEmail = usersByEmail.mapValues { entry -> entry.value.id }

        val hangouts = fakeHangouts.map { hangout ->
            HangoutEntity(
                id = hangout.id,
                title = hangout.title,
                description = hangout.description,
                locationName = hangout.locationName,
                latitude = hangout.latitude,
                longitude = hangout.longitude,
                startDate = hangout.startDate,
                endDate = hangout.endDate,
                creatorId = userIdByEmail.getValue(hangout.creator.email.normalizedEmailKey()),
                private = hangout.private
            )
        }

        val attendeeMappings = fakeHangouts.flatMap { hangout ->
            hangout.attendees
                .mapNotNull { attendee ->
                    userIdByEmail[attendee.email.normalizedEmailKey()]?.let { canonicalUserId ->
                        HangoutUsersMapping(
                            hangoutId = hangout.id,
                            userId = canonicalUserId,
                            status = attendee.attendanceStatus ?: null
                        )
                    }
                }
                .distinctBy { mapping -> mapping.userId }
        }

        database.withTransaction {
            userDao.insertUsers(users)
            hangoutDao.insertHangouts(hangouts)
            hangoutDao.insertAttendees(attendeeMappings)
        }
    }
}

private fun String.normalizedEmailKey(): String {
    return trim().lowercase(Locale.ROOT)
}
