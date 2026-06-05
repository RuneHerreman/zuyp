package be.runeherreman.zuyp.domain.usecases.api

import be.runeherreman.zuyp.domain.model.AddressSuggestion
import be.runeherreman.zuyp.domain.repository.AddressRepository
import javax.inject.Inject

class SearchAddressesUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(query: String): List<AddressSuggestion> =
        addressRepository.search(query)
}
