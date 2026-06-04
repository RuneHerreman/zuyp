package be.runeherreman.zuyp.domain.repository

import be.runeherreman.zuyp.domain.model.GeoFence

interface GeoFenceRepository {
    suspend fun replaceZones(zones: List<GeoFence>)
}
