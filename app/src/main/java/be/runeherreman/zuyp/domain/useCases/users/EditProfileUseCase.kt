package be.runeherreman.zuyp.domain.useCases.users

import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.UserRepository
import javax.inject.Inject

class EditProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) {
        userRepository.editProfile(user)
    }
}
