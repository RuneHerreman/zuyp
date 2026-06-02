package be.runeherreman.zuyp.data.local.room.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class GroupWithMembers(
    @Embedded val group: GroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = GroupUserMapping::class,
            parentColumn = "groupId",
            childColumn = "userId"
        )
    )
    val members: List<UserEntity>
)