package be.runeherreman.zuyp.domain.model

import java.util.UUID

sealed class GeofenceEvent {
    data class Entered(val hangoutId: UUID) : GeofenceEvent()
    data class Exited(val hangoutId: UUID) : GeofenceEvent()
}
