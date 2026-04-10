package be.runeherreman.zuyp.domain.model

import java.time.LocalDate
import java.util.Date
import java.util.UUID

data class User(
    val id: UUID,
    val name: String,
    val birtdate: LocalDate,
    val email: String
)