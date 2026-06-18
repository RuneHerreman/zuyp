package be.runeherreman.zuyp.data.local.room.entity.expenses

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity

data class ExpenseWithDetails(
    @Embedded val expense: ExpenseEntity,

    @Relation(parentColumn = "paidByUserId", entityColumn = "id")
    val payer: UserEntity,

    @Relation(parentColumn = "id", entityColumn = "expenseId")
    val shares: List<ExpenseShareEntity>,

    @Relation(
        parentColumn = "id", entityColumn = "id",
        associateBy = Junction(
            ExpenseShareEntity::class, parentColumn = "expenseId", entityColumn = "userId"
        )
    )
    val participants: List<UserEntity>
)