package be.runeherreman.zuyp.data.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import be.runeherreman.zuyp.data.local.room.dao.HangoutDao
import be.runeherreman.zuyp.data.local.room.entity.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.UserEntity
import be.runeherreman.zuyp.data.local.room.entity.HangoutUsersMapping

@Database(
    entities = [HangoutEntity::class, UserEntity::class, HangoutUsersMapping::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hangoutDao(): HangoutDao
}