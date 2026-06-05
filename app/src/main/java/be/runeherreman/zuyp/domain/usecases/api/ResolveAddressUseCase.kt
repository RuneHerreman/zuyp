package be.runeherreman.zuyp.domain.usecases.api

import be.runeherreman.zuyp.domain.model.ResolvedAddress
import be.runeherreman.zuyp.domain.repository.AddressRepository
import javax.inject.Inject

class ResolveAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(suggestionId: String): ResolvedAddress? =
        addressRepository.resolve(suggestionId)
}
