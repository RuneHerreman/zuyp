package be.runeherreman.zuyp.domain.model

import java.util.UUID

data class GeoFence(
    val hangoutId: UUID,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Double = DEFAULT_RADIUS_METERS,
) {
    companion object {
        const val DEFAULT_RADIUS_METERS = 100.0
    }
}