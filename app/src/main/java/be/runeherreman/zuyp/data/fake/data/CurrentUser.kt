package be.runeherreman.zuyp.data.fake.data

import be.runeherreman.zuyp.domain.model.User
import java.util.UUID

object CurrentUser {
    val user: User = FakeUsers.userKoen
    val id: UUID get() = user.id
}
