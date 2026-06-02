package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.data.fake.data.FakeFriendshipsDataSource
import be.runeherreman.zuyp.data.fake.data.FakeUsers
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject

class UserRepositoryFakeDataImpl @Inject constructor(
    private val fakeFriendshipsDataSource: FakeFriendshipsDataSource
) : UserRepository {
    override suspend fun getUserById(id: UUID): User? {
        return FakeUsers.allUsers.find { it.id == id }
    }

    override suspend fun getAllUsers(): List<User> {
        return FakeUsers.allUsers
    }

    override suspend fun areFriends(userId1: UUID, userId2: UUID): Boolean {
        return fakeFriendshipsDataSource.areFriends(userId1, userId2)
    }

    override suspend fun editProfile(user: User) {
        // No-op: fake data is read-only. The Room implementation is the bound one.
    }

    override suspend fun getFriendsOfUser(userId: UUID): List<User> {
        return fakeFriendshipsDataSource.getFriendsOfUser(userId)
    }

    override suspend fun addFriendship(userId1: UUID, userId2: UUID) {
        fakeFriendshipsDataSource.addFriendship(userId1, userId2)
    }

    override suspend fun removeFriendship(userId1: UUID, userId2: UUID) {
        fakeFriendshipsDataSource.removeFriendship(userId1, userId2)
    }
}

