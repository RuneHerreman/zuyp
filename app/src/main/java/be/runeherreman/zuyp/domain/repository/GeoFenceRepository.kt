package be.runeherreman.zuyp.domain.repository

import be.runeherreman.zuyp.domain.model.GeoFence
import be.runeherreman.zuyp.domain.model.GeofenceEvent
import kotlinx.coroutines.flow.SharedFlow

interface GeoFenceRepository {
    suspend fun replaceZones(zones: List<GeoFence>)
    fun events(): SharedFlow<GeofenceEvent>
}
