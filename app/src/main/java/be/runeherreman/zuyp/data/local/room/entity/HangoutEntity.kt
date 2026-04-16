package be.runeherreman.zuyp.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "hangouts")
data class HangoutEntity (
    @PrimaryKey val id: UUID,
    val title: String,
    val description: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val creatorId: UUID,
    val private: Boolean
)