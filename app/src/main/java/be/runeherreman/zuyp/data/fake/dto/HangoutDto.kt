package be.runeherreman.zuyp.data.fake.dto

import be.runeherreman.zuyp.domain.model.User
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

class HangoutDto (
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