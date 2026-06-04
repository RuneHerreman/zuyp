package be.runeherreman.zuyp.domain.useCases.utils.geofencing

import be.runeherreman.zuyp.domain.model.GeoFence
import be.runeherreman.zuyp.domain.repository.GeoFenceRepository
import javax.inject.Inject

class ReplaceZonesUseCase @Inject constructor(
    private val geoFenceRepository: GeoFenceRepository,
) {
    suspend operator fun invoke(zones: List<GeoFence>) {
        geoFenceRepository.replaceZones(zones)
    }
}
