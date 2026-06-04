package be.runeherreman.zuyp.data.local.room.database

import androidx.room.withTransaction
import be.runeherreman.zuyp.data.fake.data.FakeDataSource
import be.runeherreman.zuyp.data.fake.data.FakeFriendshipsDataSource
import be.runeherreman.zuyp.data.fake.data.FakeSeedingData
import be.runeherreman.zuyp.data.fake.data.FakeUsers
import be.runeherreman.zuyp.data.local.room.dao.HangoutDao
import be.runeherreman.zuyp.data.local.room.dao.UserDao
import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutUsersMapping
import be.runeherreman.zuyp.data.local.room.entity.users.FriendshipEntity
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val database: AppDatabase,
    private val hangoutDao: HangoutDao,
    private val userDao: UserDao,
    private val fakeDataSource: FakeDataSource,
    private val fakeFriendships: FakeFriendshipsDataSource
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

        // Ensure every user in FakeUsers is in the DB so friendship FKs can resolve
        val allFakeUsers = FakeUsers.allUsers.map { user ->
            UserEntity(
                id = user.id,
                name = user.name,
                birthdate = user.birthdate,
                email = user.email.trim(),
                imageUrl = user.imageUrl
            )
        }

        val friendshipEntities = fakeFriendships.getAllFriendships()
            .map { (id1, id2) -> FriendshipEntity(id1, id2) }

        database.withTransaction {
            userDao.insertUsers(allFakeUsers)
            hangoutDao.insertHangouts(hangouts)
            hangoutDao.insertAttendees(attendeeMappings)
            userDao.addFriendships(friendshipEntities)
        }
    }
}

    /** Always called on launch — keeps the test event's dates pinned to the current time. */
    suspend fun refreshLiveTestEvent() {
        val now = LocalDateTime.now()
        val entity = HangoutEntity(
            id          = FakeSeedingData.LIVE_TEST_HANGOUT_ID,
            title       = "Geofence Test Hangout",
            description = "This event always starts now and ends tomorrow for geofence testing.",
            locationName = "Howest Brugge",
            latitude    = 51.2082,
            longitude   = 3.2241,
            startDate   = now,
            endDate     = now.plusDays(1),
            creatorId   = FakeUsers.userKoen.id,
            private     = false,
        )
        hangoutDao.insert(entity)
        hangoutDao.insertOrUpdateAttendee(
            HangoutUsersMapping(
                hangoutId = FakeSeedingData.LIVE_TEST_HANGOUT_ID,
                userId    = FakeUsers.userKoen.id,
                status    = AttendanceStatus.GOING,
            )
        )
    }
}

private fun String.normalizedEmailKey(): String {
    return trim().lowercase(Locale.ROOT)
}
