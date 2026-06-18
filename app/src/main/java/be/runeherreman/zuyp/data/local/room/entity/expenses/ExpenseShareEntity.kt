package be.runeherreman.zuyp.data.local.room.entity.expenses

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import java.util.UUID

@Entity(
    tableName = "expense_shares",
    primaryKeys = ["expenseId", "userId"],
    foreignKeys = [
        ForeignKey(ExpenseEntity::class, ["id"], ["expenseId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(UserEntity::class, ["id"], ["userId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("userId")]
)
data class ExpenseShareEntity(
    val expenseId: UUID,
    val userId: UUID,
    val shareAmount: Double
)