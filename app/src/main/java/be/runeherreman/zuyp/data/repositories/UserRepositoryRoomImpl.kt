package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.data.local.room.dao.UserDao
import be.runeherreman.zuyp.data.local.room.entity.FriendshipEntity
import be.runeherreman.zuyp.data.local.room.entity.UserEntity
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject

class UserRepositoryRoomImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun getUserById(id: UUID): User? {
        return userDao.getUserById(id)?.toDomain()
    }

    override suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers().map { it.toDomain() }
    }

    override suspend fun areFriends(userId1: UUID, userId2: UUID): Boolean {
        return userDao.areFriends(userId1, userId2)
    }

    override suspend fun editProfile(user: User) {
        return userDao.editProfile(user.toEntity())
    }

    override suspend fun getFriendsOfUser(userId: UUID): List<User> {
        return userDao.getFriendsOfUser(userId).map { it.toDomain() }
    }

    override suspend fun addFriendship(userId1: UUID, userId2: UUID) {
        // Always store with the smaller UUID first for consistency
        val (first, second) = if (userId1 < userId2) {
            Pair(userId1, userId2)
        } else {
            Pair(userId2, userId1)
        }
        userDao.addFriendship(FriendshipEntity(first, second))
    }

    override suspend fun removeFriendship(userId1: UUID, userId2: UUID) {
        userDao.removeFriendship(userId1, userId2)
    }
}

private fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        birthdate = birthdate,
        email = email,
        imageUrl = imageUrl
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

