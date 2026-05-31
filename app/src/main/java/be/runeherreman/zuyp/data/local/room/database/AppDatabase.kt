package be.runeherreman.zuyp.data.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import be.runeherreman.zuyp.data.local.room.dao.HangoutDao
import be.runeherreman.zuyp.data.local.room.dao.UserDao
import be.runeherreman.zuyp.data.local.room.entity.FriendshipEntity
import be.runeherreman.zuyp.data.local.room.entity.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.UserEntity
import be.runeherreman.zuyp.data.local.room.entity.HangoutUsersMapping

@Database(
    entities = [HangoutEntity::class, UserEntity::class, HangoutUsersMapping::class, FriendshipEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hangoutDao(): HangoutDao
    abstract fun userDao(): UserDao
}
