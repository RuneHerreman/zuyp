package be.runeherreman.zuyp.domain.useCases

import be.runeherreman.zuyp.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject

class AddFriendshipUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId1: UUID, userId2: UUID) {
        userRepository.addFriendship(userId1, userId2)
    }
}

