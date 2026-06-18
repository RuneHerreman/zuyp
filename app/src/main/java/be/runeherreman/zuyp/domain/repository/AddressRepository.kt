package be.runeherreman.zuyp.domain.repository

import be.runeherreman.zuyp.domain.model.AddressSuggestion
import be.runeherreman.zuyp.domain.model.ResolvedAddress

interface AddressRepository {
    suspend fun search(query: String): List<AddressSuggestion>
    suspend fun resolve(suggestionId: String): ResolvedAddress?
}
