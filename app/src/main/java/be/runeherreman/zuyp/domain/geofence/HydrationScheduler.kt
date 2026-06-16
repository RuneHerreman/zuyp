package be.runeherreman.zuyp.domain.geofence

import java.util.UUID

interface HydrationScheduler {
    fun start(hangoutId: UUID)
    fun stop(hangoutId: UUID)
}
