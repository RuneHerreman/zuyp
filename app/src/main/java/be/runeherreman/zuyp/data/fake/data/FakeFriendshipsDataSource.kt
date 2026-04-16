package be.runeherreman.zuyp.data.fake.data

import be.runeherreman.zuyp.domain.model.User
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake data source for user friendships.
 * Manages in-memory friendship relationships.
 */
@Singleton
class FakeFriendshipsDataSource @Inject constructor() {
    // Store friendships as a set of pairs (always with smaller UUID first)
    private val friendships = mutableSetOf<Pair<UUID, UUID>>()

    init {
        // Initialize with some predefined friendships from FakeUsers
        initializeFriendships()
    }

    private fun initializeFriendships() {
        // Define some initial friendships for testing
        addFriendshipInternal(FakeUsers.userJan.id, FakeUsers.userKoen.id)
        addFriendshipInternal(FakeUsers.userJan.id, FakeUsers.userLotte.id)
        addFriendshipInternal(FakeUsers.userKoen.id, FakeUsers.userMilan.id)
        addFriendshipInternal(FakeUsers.userBram.id, FakeUsers.userElise.id)
        addFriendshipInternal(FakeUsers.userBram.id, FakeUsers.userTibo.id)
        addFriendshipInternal(FakeUsers.userMila.id, FakeUsers.userRuben.id)
        addFriendshipInternal(FakeUsers.userMila.id, FakeUsers.userNoor.id)
        addFriendshipInternal(FakeUsers.userSanne.id, FakeUsers.userDaan.id)
        addFriendshipInternal(FakeUsers.userSanne.id, FakeUsers.userLuna.id)
        addFriendshipInternal(FakeUsers.userMaxim.id, FakeUsers.userAva.id)
        addFriendshipInternal(FakeUsers.userFelix.id, FakeUsers.userZoe.id)
        addFriendshipInternal(FakeUsers.userJoren.id, FakeUsers.userIsabella.id)
        addFriendshipInternal(FakeUsers.userSebastian.id, FakeUsers.userNatasja.id)
        addFriendshipInternal(FakeUsers.userThijs.id, FakeUsers.userStéphanie.id)
        addFriendshipInternal(FakeUsers.userMarkus.id, FakeUsers.userCamille.id)
        addFriendshipInternal(FakeUsers.userVictoria.id, FakeUsers.userDieter.id)
        addFriendshipInternal(FakeUsers.userDieter.id, FakeUsers.userLea.id)
        addFriendshipInternal(FakeUsers.userRyan.id, FakeUsers.userSophie.id)
        addFriendshipInternal(FakeUsers.userQuentin.id, FakeUsers.userEva.id)
        addFriendshipInternal(FakeUsers.userLars.id, FakeUsers.userAnna.id)
        addFriendshipInternal(FakeUsers.userTom.id, FakeUsers.userEmilie.id)
        addFriendshipInternal(FakeUsers.userPhilip.id, FakeUsers.userClaire.id)
        addFriendshipInternal(FakeUsers.userSven.id, FakeUsers.userBeat.id)
        addFriendshipInternal(FakeUsers.userBeat.id, FakeUsers.userLena.id)
        addFriendshipInternal(FakeUsers.userJulian.id, FakeUsers.userSienna.id)
        addFriendshipInternal(FakeUsers.userSienna.id, FakeUsers.userAlex.id)
        addFriendshipInternal(FakeUsers.userOliver.id, FakeUsers.userMaya.id)
        addFriendshipInternal(FakeUsers.userMaya.id, FakeUsers.userLuc.id)
        addFriendshipInternal(FakeUsers.userLuc.id, FakeUsers.userAnne.id)
        addFriendshipInternal(FakeUsers.userDavid.id, FakeUsers.userFlorence.id)
        addFriendshipInternal(FakeUsers.userNico.id, FakeUsers.userGrace.id)
        addFriendshipInternal(FakeUsers.userChris.id, FakeUsers.userMaria.id)
        addFriendshipInternal(FakeUsers.userSteve.id, FakeUsers.userJessica.id)
        addFriendshipInternal(FakeUsers.userJessica.id, FakeUsers.userPaul.id)
        addFriendshipInternal(FakeUsers.userPaul.id, FakeUsers.userRosa.id)
    }

    private fun addFriendshipInternal(userId1: UUID, userId2: UUID) {
        // Always store with the smaller UUID first for consistency
        val (first, second) = if (userId1 < userId2) {
            Pair(userId1, userId2)
        } else {
            Pair(userId2, userId1)
        }
        friendships.add(first to second)
    }

    fun areFriends(userId1: UUID, userId2: UUID): Boolean {
        val (first, second) = if (userId1 < userId2) {
            Pair(userId1, userId2)
        } else {
            Pair(userId2, userId1)
        }
        return friendships.contains(first to second)
    }

    fun getFriendsOfUser(userId: UUID): List<User> {
        return friendships
            .filter { (first, second) ->
                first == userId || second == userId
            }
            .mapNotNull { (first, second) ->
                val friendId = if (first == userId) second else first
                FakeUsers.allUsers.find { it.id == friendId }
            }
    }

    fun addFriendship(userId1: UUID, userId2: UUID) {
        addFriendshipInternal(userId1, userId2)
    }

    fun removeFriendship(userId1: UUID, userId2: UUID) {
        val (first, second) = if (userId1 < userId2) {
            Pair(userId1, userId2)
        } else {
            Pair(userId2, userId1)
        }
        friendships.remove(first to second)
    }
}

