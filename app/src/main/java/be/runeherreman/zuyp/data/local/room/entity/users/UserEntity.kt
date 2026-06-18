package be.runeherreman.zuyp.data.local.room.entity.users

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity (
    @PrimaryKey val id: UUID,
    val name: String,
    val birthdate: LocalDate,
    val email: String,
    val imageUrl: String = ""
)
