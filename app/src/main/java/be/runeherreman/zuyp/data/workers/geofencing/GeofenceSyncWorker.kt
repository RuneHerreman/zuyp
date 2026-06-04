package be.runeherreman.zuyp.data.workers.geofencing

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import be.runeherreman.zuyp.domain.repository.GeoFenceRepository
import be.runeherreman.zuyp.domain.useCases.utils.geofencing.GetActiveGeofenceZonesUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class GeofenceSyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Dependencies {
        fun getActiveGeofenceZonesUseCase(): GetActiveGeofenceZonesUseCase
        fun geoFenceRepository(): GeoFenceRepository
    }

    override suspend fun doWork(): Result {
        val deps = EntryPointAccessors.fromApplication(applicationContext, Dependencies::class.java)
        val zones = deps.getActiveGeofenceZonesUseCase().getSnapshot()
        deps.geoFenceRepository().replaceZones(zones)
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "geofence_sync"
    }
}
