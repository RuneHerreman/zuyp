package be.runeherreman.zuyp.domain.useCases

import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject

class GetUserByIdUserCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: UUID): User? {
        return userRepository.getUserById(userId)
    }
}