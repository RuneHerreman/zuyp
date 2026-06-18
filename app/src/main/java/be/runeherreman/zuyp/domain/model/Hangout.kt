package be.runeherreman.zuyp.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Hangout(
    val id: UUID,
    val title: String,
    val description: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val attendees: List<User>,
    val creator: User,
    val private: Boolean
)