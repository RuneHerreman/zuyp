package be.runeherreman.zuyp.domain.usecases.friendship

import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject

class GetFriendsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: UUID): List<User> {
        return userRepository.getFriendsOfUser(userId)
    }
}

