package be.runeherreman.zuyp.domain.geofence

import be.runeherreman.zuyp.domain.model.GeofenceEvent
import be.runeherreman.zuyp.domain.repository.GeoFenceRepository
import be.runeherreman.zuyp.domain.usecases.geofencing.GetActiveGeofenceZonesUseCase
import be.runeherreman.zuyp.domain.usecases.hangouts.MarkLeftUseCase
import be.runeherreman.zuyp.domain.usecases.hangouts.MarkPresentUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceSyncCoordinator @Inject constructor(
    private val getActiveGeofenceZonesUseCase: GetActiveGeofenceZonesUseCase,
    private val geoFenceRepository: GeoFenceRepository,
    private val geofenceSyncScheduler: GeofenceSyncScheduler,
    private val hydrationScheduler: HydrationScheduler,
    private val markPresentUseCase: MarkPresentUseCase,
    private val markLeftUseCase: MarkLeftUseCase,
) {
    fun start(scope: CoroutineScope, currentUserId: UUID) {
        scope.launch {
            geoFenceRepository.events()
                .onSubscription { scope.launch { syncZones() } }
                .collect { event ->
                    when (event) {
                        is GeofenceEvent.Entered -> onUserEntered(event.hangoutId, currentUserId)
                        is GeofenceEvent.Exited  -> onUserLeft(event.hangoutId, currentUserId)
                    }
                }
        }
        geofenceSyncScheduler.schedulePeriodicSync()
    }

    private suspend fun syncZones() {
        getActiveGeofenceZonesUseCase.getActiveGeofenceZones()
            .distinctUntilChanged()
            .collect { zones -> geoFenceRepository.replaceZones(zones) }
    }

    private suspend fun onUserEntered(hangoutId: UUID, userId: UUID) {
        if (markPresentUseCase(hangoutId, userId)) {
            hydrationScheduler.start(hangoutId)
        }
    }

    private suspend fun onUserLeft(hangoutId: UUID, userId: UUID) {
        markLeftUseCase(hangoutId, userId)
        hydrationScheduler.stop(hangoutId)
    }
}
