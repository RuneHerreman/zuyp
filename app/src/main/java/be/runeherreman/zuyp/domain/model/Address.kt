package be.runeherreman.zuyp.domain.model

data class AddressSuggestion(
    val id: String,
    val name: String,
    val fullAddress: String
)

data class ResolvedAddress(
    val name: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double
)
