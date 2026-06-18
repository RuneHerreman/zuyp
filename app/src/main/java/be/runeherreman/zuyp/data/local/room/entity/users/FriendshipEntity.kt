package be.runeherreman.zuyp.data.local.room.entity.users

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "friendships",
    primaryKeys = ["userId1", "userId2"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId1"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId2"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId1"),
        Index("userId2")
    ]
)
data class FriendshipEntity(
    val userId1: UUID,
    val userId2: UUID
)

