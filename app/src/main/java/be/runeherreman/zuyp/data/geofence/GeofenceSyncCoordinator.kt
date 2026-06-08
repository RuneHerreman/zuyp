package be.runeherreman.zuyp.data.geofence

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.data.workers.geofencing.GeofenceSyncWorker
import be.runeherreman.zuyp.data.workers.geofencing.HydrationReminderScheduler
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceSyncCoordinator @Inject constructor(
    private val getActiveGeofenceZonesUseCase: GetActiveGeofenceZonesUseCase,
    private val geoFenceRepository: GeoFenceRepository,
    private val workManager: WorkManager,
    private val hydrationScheduler: HydrationReminderScheduler,
    private val markPresentUseCase: MarkPresentUseCase,
    private val markLeftUseCase: MarkLeftUseCase,
) {
    fun start(scope: CoroutineScope) {
        scope.launch {
            geoFenceRepository.events()
                .onSubscription { scope.launch { syncZones() } }
                .collect { event ->
                    when (event) {
                        is GeofenceEvent.Entered -> onUserEntered(event.hangoutId)
                        is GeofenceEvent.Exited  -> onUserLeft(event.hangoutId)
                    }
                }
        }
        schedulePeriodicSync()
    }

    // Keep in sync with current events from data
    private suspend fun syncZones() {
        getActiveGeofenceZonesUseCase.getActiveGeofenceZones()
            .distinctUntilChanged()
            .collect { zones -> geoFenceRepository.replaceZones(zones) }
    }

    private suspend fun onUserEntered(hangoutId: UUID) {
        if (markPresentUseCase(hangoutId, CurrentUser.id)) {
            hydrationScheduler.start(hangoutId)
        }
    }

    private suspend fun onUserLeft(hangoutId: UUID) {
        markLeftUseCase(hangoutId, CurrentUser.id)
        hydrationScheduler.stop(hangoutId)
    }

    private fun schedulePeriodicSync() {
        workManager.enqueueUniquePeriodicWork(
            GeofenceSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<GeofenceSyncWorker>(15, TimeUnit.MINUTES).build(),
        )
    }
}
