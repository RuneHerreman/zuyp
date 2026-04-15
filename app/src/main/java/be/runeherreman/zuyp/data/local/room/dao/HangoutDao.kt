package be.runeherreman.zuyp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import be.runeherreman.zuyp.data.local.room.entity.HangoutUsersMapping
import be.runeherreman.zuyp.data.local.room.entity.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.HangoutWithDetails
import be.runeherreman.zuyp.data.local.room.entity.UserEntity
import java.util.UUID

@Dao
interface HangoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(items: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHangouts(items: List<HangoutEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendees(crossRefs: List<HangoutUsersMapping>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HangoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendee(crossRef: HangoutUsersMapping)

    @Transaction
    @Query("SELECT * FROM hangouts")
    suspend fun getAll(): List<HangoutWithDetails>

    @Transaction
    @Query("SELECT * FROM hangouts WHERE id = :id")
    suspend fun getById(id: UUID): HangoutWithDetails?

    @Query("SELECT COUNT(*) FROM hangouts")
    suspend fun countHangouts(): Int

    @Query("DELETE FROM hangouts WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM hangouts_users")
    suspend fun deleteAllAttendees()

    @Query("DELETE FROM hangouts")
    suspend fun deleteAllHangouts()

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}