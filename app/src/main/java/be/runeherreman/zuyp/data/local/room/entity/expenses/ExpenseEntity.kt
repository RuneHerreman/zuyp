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
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(HangoutEntity::class, ["id"], ["hangoutId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(UserEntity::class, ["id"], ["paidByUserId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("hangoutId"), Index("paidByUserId")]
)
data class ExpenseEntity(
    @PrimaryKey val id: UUID,
    val hangoutId: UUID,
    val paidByUserId: UUID,
    val title: String,
    val amount: Double,
    val imageUri: String?,
    val createdAt: LocalDateTime
)