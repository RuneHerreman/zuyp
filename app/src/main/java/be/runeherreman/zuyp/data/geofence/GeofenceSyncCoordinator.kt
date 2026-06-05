package be.runeherreman.zuyp.data.geofence

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import be.runeherreman.zuyp.data.workers.geofencing.GeofenceSyncWorker
import be.runeherreman.zuyp.domain.useCases.api.geofencing.GetActiveGeofenceZonesUseCase
import be.runeherreman.zuyp.domain.useCases.api.geofencing.ReplaceZonesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceSyncCoordinator @Inject constructor(
    private val getActiveGeofenceZonesUseCase: GetActiveGeofenceZonesUseCase,
    private val replaceZonesUseCase: ReplaceZonesUseCase,
    private val workManager: WorkManager,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        collectDataChanges()
        schedulePeriodicSync()
    }

    private fun collectDataChanges() {
        scope.launch {
            getActiveGeofenceZonesUseCase.getActiveGeofenceZones()
                .distinctUntilChanged()
                .collect { zones -> replaceZonesUseCase(zones) }
        }
    }

    private fun schedulePeriodicSync() {
        val request = PeriodicWorkRequestBuilder<GeofenceSyncWorker>(15, TimeUnit.MINUTES).build()
        workManager.enqueueUniquePeriodicWork(
            GeofenceSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
