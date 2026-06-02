package be.runeherreman.zuyp.domain.model

import java.util.UUID

data class Group(
    val id: UUID,
    val creatorId: UUID,
    val name: String,
    val description: String,
    val members: List<User>,
    val imageUrl: String = ""
)