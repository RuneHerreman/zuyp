package be.runeherreman.zuyp.data.local.room.entity

import androidx.room.Entity
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "hangouts_users",
    primaryKeys = ["hangoutId", "userId"],
    indices = [Index(value = ["userId"])]
)
data class HangoutUsersMapping(
    val hangoutId: UUID,
    val userId: UUID
)
