package be.runeherreman.zuyp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import be.runeherreman.zuyp.data.local.room.entity.HangoutUsersMapping
import be.runeherreman.zuyp.data.local.room.entity.AttendanceStatus
import be.runeherreman.zuyp.data.local.room.entity.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.HangoutWithDetails
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface HangoutDao {
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
    fun getAll(): Flow<List<HangoutWithDetails>>

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

    @Query("UPDATE hangouts_users SET status = :status WHERE hangoutId = :hangoutId AND userId = :userId")
    suspend fun updateAttendanceStatus(hangoutId: UUID, userId: UUID, status: AttendanceStatus)

    @Query("DELETE FROM hangouts_users WHERE hangoutId = :hangoutId AND userId = :userId")
    suspend fun removeAttendee(hangoutId: UUID, userId: UUID)
}