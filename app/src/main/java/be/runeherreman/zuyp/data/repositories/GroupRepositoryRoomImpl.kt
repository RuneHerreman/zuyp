package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.data.local.room.dao.GroupDao
import be.runeherreman.zuyp.data.local.room.entity.groups.GroupEntity
import be.runeherreman.zuyp.data.local.room.entity.groups.GroupUserMapping
import be.runeherreman.zuyp.data.local.room.entity.groups.GroupWithMembers
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class GroupRepositoryRoomImpl @Inject constructor(
    private val groupDao: GroupDao
) : GroupRepository {
    override fun getUserGroups(userId: UUID): Flow<List<Group>> {
        return groupDao.getGroupsForUser(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getGroupById(groupId: UUID): Group? {
        return groupDao.getGroupById(groupId)?.toDomain()
    }

    override suspend fun createGroup(group: Group) {
        groupDao.createGroup(group.toEntity())
        // Persist the creator plus every picked member (deduped in case creator is also in the members list).
        val memberIds = (listOf(group.creatorId) + group.members.map { it.id }).distinct()
        groupDao.addMembers(memberIds.map { GroupUserMapping(group.id, it) })
    }

    override suspend fun renameGroup(groupId: UUID, name: String, requesterId: UUID) {
        val groupWithMembers = groupDao.getGroupById(groupId) ?: return
        if (groupWithMembers.group.creatorId == requesterId) {
            groupDao.renameGroup(groupId, name)
        }
    }

    override suspend fun removeGroup(groupId: UUID, requesterId: UUID) {
        val groupWithMembers = groupDao.getGroupById(groupId) ?: return
        if (groupWithMembers.group.creatorId == requesterId) {
            groupDao.deleteGroup(groupId)
        }
    }

    override suspend fun addMember(groupId: UUID, userId: UUID) {
        groupDao.addMembers(listOf(GroupUserMapping(groupId, userId)))
    }

    override suspend fun removeMember(groupId: UUID, memberId: UUID, requesterId: UUID) {
        val groupWithMembers = groupDao.getGroupById(groupId) ?: return
        if (requesterId == groupWithMembers.group.creatorId || requesterId == memberId) {
            groupDao.removeMember(groupId, memberId)
        }
    }
}

fun GroupWithMembers.toDomain(): Group {
    return Group(
        id = group.id,
        name = group.name,
        creatorId = group.creatorId,
        description = group.description,
        members = members.map { it.toDomain() }
    )
}

private fun Group.toEntity(): GroupEntity {
    return GroupEntity(
        id = id,
        name = name,
        creatorId = creatorId,
        description = description,
        imageUrl = "" // Assuming default or property to be added to domain if needed
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
