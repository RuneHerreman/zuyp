package be.runeherreman.zuyp.data.local.room.entity.hangouts

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import be.runeherreman.zuyp.domain.model.AttendanceStatus
import java.util.UUID

@Entity(
    tableName = "hangouts_users",
    primaryKeys = ["hangoutId", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = HangoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["hangoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class HangoutUsersMapping(
    val hangoutId: UUID,
    val userId: UUID,
    val status: AttendanceStatus?
)
