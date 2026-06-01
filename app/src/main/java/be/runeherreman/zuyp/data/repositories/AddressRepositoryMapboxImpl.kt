package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.domain.model.AddressSuggestion
import be.runeherreman.zuyp.domain.model.ResolvedAddress
import be.runeherreman.zuyp.domain.repository.AddressRepository
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import javax.inject.Inject

/**
 * Address lookup backed by the Mapbox Search SDK [PlaceAutocomplete] engine.
 *
 * The SDK's `select()` step needs the original [PlaceAutocompleteSuggestion]
 * object (not just a string), so we cache the most recent batch of suggestions
 * keyed by the opaque id we hand out, and look them back up on [resolve].
 */
class AddressRepositoryMapboxImpl @Inject constructor(
    private val placeAutocomplete: PlaceAutocomplete
) : AddressRepository {

    private var suggestionCache: Map<String, PlaceAutocompleteSuggestion> = emptyMap()

    override suspend fun search(query: String): List<AddressSuggestion> {
        if (query.isBlank()) return emptyList()

        val suggestions = placeAutocomplete.suggestions(query).value ?: emptyList()

        val keyed = suggestions.mapIndexed { index, suggestion -> index.toString() to suggestion }
        suggestionCache = keyed.toMap()

        return keyed.map { (id, suggestion) ->
            AddressSuggestion(
                id = id,
                name = suggestion.name,
                fullAddress = suggestion.formattedAddress ?: suggestion.name
            )
        }
    }

    override suspend fun resolve(suggestionId: String): ResolvedAddress? {
        val suggestion = suggestionCache[suggestionId] ?: return null
        val result = placeAutocomplete.select(suggestion).value ?: return null
        val point = result.coordinate

        return ResolvedAddress(
            name = result.name,
            fullAddress = suggestion.formattedAddress ?: result.name,
            latitude = point.latitude(),
            longitude = point.longitude()
        )
    }
}
