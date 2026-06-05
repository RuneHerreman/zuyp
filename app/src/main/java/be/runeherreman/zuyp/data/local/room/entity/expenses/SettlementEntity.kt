package be.runeherreman.zuyp.data.local.room.entity.expenses

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "expense_settlements",
    foreignKeys = [
        ForeignKey(HangoutEntity::class, ["id"], ["hangoutId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(UserEntity::class, ["id"], ["fromUserId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(UserEntity::class, ["id"], ["toUserId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [
        Index("hangoutId"),
        Index("fromUserId"),
        Index("toUserId"),
    ]
)

data class SettlementEntity(
    @PrimaryKey val id: UUID,
    val hangoutId: UUID,
    val fromUserId: UUID,
    val toUserId: UUID,
    val amount: Double,
    val settledAt: LocalDateTime
)