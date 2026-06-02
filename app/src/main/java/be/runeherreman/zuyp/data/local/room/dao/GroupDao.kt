package be.runeherreman.zuyp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import be.runeherreman.zuyp.data.local.room.entity.GroupEntity
import be.runeherreman.zuyp.data.local.room.entity.GroupUserMapping
import be.runeherreman.zuyp.data.local.room.entity.GroupWithMembers
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createGroup(group: GroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMembers(members: List<GroupUserMapping>)
    
    @Query("DELETE FROM groups_users WHERE groupId = :groupId AND userId = :userId")
    suspend fun removeMember(groupId: UUID, userId: UUID)

    @Query("DELETE FROM `groups` WHERE id = :groupId")
    suspend fun deleteGroup(groupId: UUID)

    @Transaction
    @Query("SELECT * FROM `groups` WHERE id IN (SELECT groupId FROM groups_users WHERE userId = :userId)")
    fun getGroupsForUser(userId: UUID): Flow<List<GroupWithMembers>>

    @Transaction
    @Query("SELECT * FROM `groups` WHERE id = :groupId")
    suspend fun getGroupById(groupId: UUID): GroupWithMembers?
}