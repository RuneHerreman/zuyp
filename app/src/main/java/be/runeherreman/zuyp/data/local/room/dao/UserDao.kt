package be.runeherreman.zuyp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import be.runeherreman.zuyp.data.local.room.entity.FriendshipEntity
import be.runeherreman.zuyp.data.local.room.entity.UserEntity
import be.runeherreman.zuyp.domain.model.User
import java.util.UUID

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: UUID): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: UUID)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    // Friendship queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFriendship(friendship: FriendshipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFriendships(friendships: List<FriendshipEntity>)

    @Query(
        "SELECT EXISTS(SELECT 1 FROM friendships " +
        "WHERE (userId1 = :userId1 AND userId2 = :userId2) OR " +
        "(userId1 = :userId2 AND userId2 = :userId1))"
    )
    suspend fun areFriends(userId1: UUID, userId2: UUID): Boolean

    @Query(
        "SELECT u.* FROM users u " +
        "INNER JOIN friendships f ON (" +
        "(f.userId1 = :userId AND u.id = f.userId2) OR " +
        "(f.userId2 = :userId AND u.id = f.userId1)" +
        ") " +
        "ORDER BY u.name"
    )
    suspend fun getFriendsOfUser(userId: UUID): List<UserEntity>

    @Query(
        "SELECT COUNT(*) FROM friendships " +
        "WHERE userId1 = :userId OR userId2 = :userId"
    )
    suspend fun getFriendCount(userId: UUID): Int

    @Query(
        "DELETE FROM friendships " +
        "WHERE (userId1 = :userId1 AND userId2 = :userId2) OR " +
        "(userId1 = :userId2 AND userId2 = :userId1)"
    )
    suspend fun removeFriendship(userId1: UUID, userId2: UUID)

    @Query("DELETE FROM friendships")
    suspend fun deleteAllFriendships()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun editProfile(user: UserEntity)
}

