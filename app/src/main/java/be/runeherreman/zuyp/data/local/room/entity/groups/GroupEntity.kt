package be.runeherreman.zuyp.data.local.room.entity.groups

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import java.util.UUID

@Entity(
    tableName = "groups",
    indices = [Index(value = ["id"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["creatorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GroupEntity(
    @PrimaryKey val id: UUID,
    val name: String,
    val creatorId: UUID,
    val imageUrl: String = "",
    val description: String = ""
)