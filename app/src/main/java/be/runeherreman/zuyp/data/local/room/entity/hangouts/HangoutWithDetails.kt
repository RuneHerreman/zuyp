package be.runeherreman.zuyp.data.local.room.entity.hangouts

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity

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
    val attendees: List<UserEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "hangoutId"
    )
    val attendanceStatuses: List<HangoutUsersMapping>
)

