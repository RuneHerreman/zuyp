package be.runeherreman.zuyp.domain.repository

import be.runeherreman.zuyp.domain.model.User
import java.util.UUID

interface UserRepository {
    suspend fun getUserById(id: UUID): User?
    suspend fun getAllUsers(): List<User>
    suspend fun areFriends(userId1: UUID, userId2: UUID): Boolean
    suspend fun getFriendsOfUser(userId: UUID): List<User>
    suspend fun addFriendship(userId1: UUID, userId2: UUID)
    suspend fun removeFriendship(userId1: UUID, userId2: UUID)
}

