package be.runeherreman.zuyp.domain.model

/**
 * A single address suggestion returned while the user is typing.
 * [id] is an opaque handle used to resolve the full address later — it has no
 * meaning outside the repository that produced it.
 */
data class AddressSuggestion(
    val id: String,
    val name: String,
    val fullAddress: String
)

/**
 * A fully resolved, existing address with coordinates.
 * Only addresses that resolve to one of these are considered valid.
 */
data class ResolvedAddress(
    val name: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double
)
