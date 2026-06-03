package be.runeherreman.zuyp.domain.model

import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
import java.time.LocalDate
import java.util.UUID

data class User(
    val id: UUID,
    val name: String,
    val birthdate: LocalDate,
    val email: String,
    val imageUrl: String = "",
    val attendanceStatus: AttendanceStatus? = null
)