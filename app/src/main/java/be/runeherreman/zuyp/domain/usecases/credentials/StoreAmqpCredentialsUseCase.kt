package be.runeherreman.zuyp.domain.usecases.credentials

import be.runeherreman.zuyp.domain.model.AmqpCredentials
import be.runeherreman.zuyp.domain.repository.CredentialsRepository
import javax.inject.Inject

class StoreAmqpCredentialsUseCase @Inject constructor(
    private val credentialsRepository: CredentialsRepository
) {
    operator fun invoke(credentials: AmqpCredentials) =
        credentialsRepository.storeAmqpCredentials(credentials)
}