package be.runeherreman.zuyp.data.repositories

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import be.runeherreman.zuyp.data.workers.geofencing.HydrationReminderScheduler
import be.runeherreman.zuyp.data.workers.geofencing.MarkPresentWorker
import be.runeherreman.zuyp.domain.model.GeoFence
import be.runeherreman.zuyp.domain.repository.GeoFenceRepository
import com.google.gson.JsonPrimitive
import com.mapbox.annotation.MapboxExperimental
import com.mapbox.common.geofencing.GeofencingError
import com.mapbox.common.geofencing.GeofencingEvent
import com.mapbox.common.geofencing.GeofencingFactory
import com.mapbox.common.geofencing.GeofencingObserver
import com.mapbox.common.geofencing.GeofencingPropertiesKeys
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfTransformation
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(MapboxExperimental::class)
@Singleton
class GeofenceRepositoryMapboxImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager,
    private val hydrationScheduler: HydrationReminderScheduler,
) : GeoFenceRepository {

    private val geofencing by lazy { GeofencingFactory.getOrCreate() }
    private val registeredIds = mutableSetOf<String>()

    private val observer = object : GeofencingObserver {
        override fun onEntry(event: GeofencingEvent) = Unit
        override fun onDwell(event: GeofencingEvent) = onUserDwelled(event)
        override fun onExit(event: GeofencingEvent) = onUserLeft(event)
        override fun onError(error: GeofencingError) { Log.w(TAG, "error: $error") }
        override fun onUserConsentChanged(isConsentGiven: Boolean) = Unit
    }

    init {
        geofencing.addObserver(observer) { error -> Log.w(TAG, "addObserver failed: $error") }
    }

    override suspend fun replaceZones(zones: List<GeoFence>) {
        val newIds = zones.map { it.hangoutId.toString() }.toSet()

        (registeredIds - newIds).forEach { id ->
            geofencing.removeFeature(id) { error -> Log.w(TAG, "removeFeature $id failed: $error") }
        }

        zones.filter { it.hangoutId.toString() !in registeredIds }.forEach { zone ->
            geofencing.addFeature(zone.toFeature()) { error -> Log.w(TAG, "addFeature failed: $error") }
        }

        registeredIds.clear()
        registeredIds.addAll(newIds)
    }

    private fun onUserDwelled(event: GeofencingEvent) {
        val hangoutId = event.feature.id() ?: return
        workManager.enqueue(
            OneTimeWorkRequestBuilder<MarkPresentWorker>()
                .setInputData(workDataOf(MarkPresentWorker.KEY_HANGOUT_ID to hangoutId))
                .build()
        )
    }

    private fun onUserLeft(event: GeofencingEvent) {
        val hangoutId = event.feature.id() ?: return
        hydrationScheduler.stop(hangoutId)
    }

    private fun GeoFence.toFeature(): Feature {
        val circle = TurfTransformation.circle(
            Point.fromLngLat(longitude, latitude),
            radiusMeters,
            64,
            TurfConstants.UNIT_METERS,
        )
        return Feature.fromGeometry(circle, null, hangoutId.toString()).apply {
            addProperty(GeofencingPropertiesKeys.DWELL_TIME_KEY, JsonPrimitive(DWELL_MINUTES))
        }
    }

    companion object {
        private const val TAG           = "GeofenceRepository"
        private const val DWELL_MINUTES = 0
    }
}
