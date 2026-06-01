package be.runeherreman.zuyp.domain.repository

import be.runeherreman.zuyp.domain.model.AddressSuggestion
import be.runeherreman.zuyp.domain.model.ResolvedAddress

interface AddressRepository {
    /** Returns address suggestions for the given (partial) query. */
    suspend fun search(query: String): List<AddressSuggestion>

    /**
     * Resolves a previously returned [AddressSuggestion.id] into a full address
     * with coordinates, or null if it can no longer be resolved.
     */
    suspend fun resolve(suggestionId: String): ResolvedAddress?
}
