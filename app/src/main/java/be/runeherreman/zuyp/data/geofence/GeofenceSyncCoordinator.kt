package be.runeherreman.zuyp.data.geofence

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.data.workers.geofencing.GeofenceSyncWorker
import be.runeherreman.zuyp.data.workers.geofencing.HydrationReminderScheduler
import be.runeherreman.zuyp.data.workers.geofencing.MarkPresentWorker
import be.runeherreman.zuyp.domain.model.GeofenceEvent
import be.runeherreman.zuyp.domain.repository.GeoFenceRepository
import be.runeherreman.zuyp.domain.usecases.geofencing.GetActiveGeofenceZonesUseCase
import be.runeherreman.zuyp.domain.usecases.geofencing.ReplaceZonesUseCase
import be.runeherreman.zuyp.domain.usecases.hangouts.MarkLeftUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceSyncCoordinator @Inject constructor(
    private val getActiveGeofenceZonesUseCase: GetActiveGeofenceZonesUseCase,
    private val replaceZonesUseCase: ReplaceZonesUseCase,
    private val geoFenceRepository: GeoFenceRepository,
    private val workManager: WorkManager,
    private val hydrationScheduler: HydrationReminderScheduler,
    private val markLeftUseCase: MarkLeftUseCase,
) {
    fun start(scope: CoroutineScope) {
        scope.launch { syncZones() }
        scope.launch { handleGeofenceEvents() }
        schedulePeriodicSync()
    }

    // Keep in sync with current events from data
    private suspend fun syncZones() {
        getActiveGeofenceZonesUseCase.getActiveGeofenceZones()
            .distinctUntilChanged()
            .collect { zones -> replaceZonesUseCase(zones) }
    }

    // handles entry / exit
    private suspend fun handleGeofenceEvents() {
        geoFenceRepository.events().collect { event ->
            when (event) {
                is GeofenceEvent.Entered -> onUserEntered(event.hangoutId)
                is GeofenceEvent.Exited  -> onUserLeft(event.hangoutId)
            }
        }
    }

    // mark present, start hydration reminders
    private fun onUserEntered(hangoutId: UUID) {
        workManager.enqueue(
            OneTimeWorkRequestBuilder<MarkPresentWorker>()
                .setInputData(workDataOf(MarkPresentWorker.KEY_HANGOUT_ID to hangoutId.toString()))
                .build()
        )
    }

    private suspend fun onUserLeft(hangoutId: UUID) {
        hydrationScheduler.stop(hangoutId)
        markLeftUseCase(hangoutId, CurrentUser.id)
    }

    private fun schedulePeriodicSync() {
        workManager.enqueueUniquePeriodicWork(
            GeofenceSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<GeofenceSyncWorker>(15, TimeUnit.MINUTES).build(),
        )
    }
}
