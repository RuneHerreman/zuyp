package be.runeherreman.zuyp.data.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import be.runeherreman.zuyp.data.local.room.dao.ExpenseDao
import be.runeherreman.zuyp.data.local.room.dao.GroupDao
import be.runeherreman.zuyp.data.local.room.dao.HangoutDao
import be.runeherreman.zuyp.data.local.room.dao.UserDao
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseEntity
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseShareEntity
import be.runeherreman.zuyp.data.local.room.entity.expenses.SettlementEntity
import be.runeherreman.zuyp.data.local.room.entity.users.FriendshipEntity
import be.runeherreman.zuyp.data.local.room.entity.groups.GroupEntity
import be.runeherreman.zuyp.data.local.room.entity.groups.GroupUserMapping
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutUsersMapping

@Database(
    entities = [
        HangoutEntity::class,
        UserEntity::class,
        HangoutUsersMapping::class,
        FriendshipEntity::class,
        GroupEntity::class,
        GroupUserMapping::class,
        ExpenseEntity::class,
        ExpenseShareEntity::class,
        SettlementEntity::class
   ],
    version = 12,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hangoutDao(): HangoutDao
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun expenseDao(): ExpenseDao
}
