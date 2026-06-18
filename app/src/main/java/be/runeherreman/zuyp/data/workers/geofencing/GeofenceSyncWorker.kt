package be.runeherreman.zuyp.data.workers.geofencing

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import be.runeherreman.zuyp.domain.repository.GeoFenceRepository
import be.runeherreman.zuyp.domain.usecases.geofencing.GetActiveGeofenceZonesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GeofenceSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getActiveGeofenceZonesUseCase: GetActiveGeofenceZonesUseCase,
    private val geoFenceRepository: GeoFenceRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val zones = getActiveGeofenceZonesUseCase.getSnapshot()
        geoFenceRepository.replaceZones(zones)
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "geofence_sync"
    }
}
