package be.runeherreman.zuyp.domain.model

import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

data class Hangout(
    val id: UUID,
    val title: String,
    val description: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val date: LocalDateTime,
    val attendees: List<User>,
    val creator: User,
    val private: Boolean
)