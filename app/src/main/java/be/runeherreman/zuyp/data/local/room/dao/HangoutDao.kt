package be.runeherreman.zuyp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutUsersMapping
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutWithDetails
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface HangoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHangouts(items: List<HangoutEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendees(hangoutUsers: List<HangoutUsersMapping>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HangoutEntity)

    @Transaction
    suspend fun insertHangoutWithAttendees(hangout: HangoutEntity, mapping: List<HangoutUsersMapping>) {
        insert(hangout)
        insertAttendees(mapping)
    }

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendee(hangoutUser: HangoutUsersMapping)

    @Query("DELETE FROM hangouts_users WHERE hangoutId = :hangoutId AND userId = :userId")
    suspend fun removeAttendee(hangoutId: UUID, userId: UUID)

    @Query("DELETE FROM hangouts WHERE id = :hangoutId")
    suspend fun removeHangout(hangoutId: UUID)
}