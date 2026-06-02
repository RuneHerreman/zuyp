package be.runeherreman.zuyp.domain.repository

import be.runeherreman.zuyp.domain.model.Group
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface GroupRepository {
    fun getUserGroups(userId: UUID): Flow<List<Group>>
    suspend fun getGroupById(groupId: UUID): Group?
    suspend fun createGroup(group: Group)
    suspend fun removeGroup(groupId: UUID, requesterId: UUID)
    suspend fun addMember(groupId: UUID, userId: UUID)
    suspend fun removeMember(groupId: UUID, memberId: UUID, requesterId: UUID)
}