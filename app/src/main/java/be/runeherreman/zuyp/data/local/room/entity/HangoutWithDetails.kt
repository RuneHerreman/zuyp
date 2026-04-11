package be.runeherreman.zuyp.data.local.room.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class HangoutWithDetails(
    @Embedded val hangout: HangoutEntity,

    @Relation(
        parentColumn = "creatorId",
        entityColumn = "id"
    )
    val creator: UserEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = HangoutUsersMapping::class,
            parentColumn = "hangoutId",
            entityColumn = "userId"
        )
    )
    val attendees: List<UserEntity>
)

