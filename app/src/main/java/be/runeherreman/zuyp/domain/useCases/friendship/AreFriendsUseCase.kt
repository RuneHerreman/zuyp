package be.runeherreman.zuyp.domain.useCases.friendship

import be.runeherreman.zuyp.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject

class AreFriendsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId1: UUID, userId2: UUID): Boolean {
        return userRepository.areFriends(userId1, userId2)
    }
}