package be.runeherreman.zuyp.data.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.util.Log
import be.runeherreman.zuyp.domain.model.GeoFence
import be.runeherreman.zuyp.domain.model.GeofenceEvent
import be.runeherreman.zuyp.domain.repository.GeoFenceRepository
import be.runeherreman.zuyp.ui.permissions.AppPermission
import be.runeherreman.zuyp.ui.permissions.isGranted
import com.mapbox.annotation.MapboxExperimental
import com.mapbox.common.geofencing.GeofencingError
import com.mapbox.common.geofencing.GeofencingEvent
import com.mapbox.common.geofencing.GeofencingFactory
import com.mapbox.common.geofencing.GeofencingObserver
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.mapbox.turf.TurfTransformation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Collections
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(MapboxExperimental::class)
@Singleton
class GeofenceRepositoryMapboxImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : GeoFenceRepository {

    private val _events = MutableSharedFlow<GeofenceEvent>(extraBufferCapacity = 16)
    override fun events(): SharedFlow<GeofenceEvent> = _events.asSharedFlow()

    private val geofencing by lazy { GeofencingFactory.getOrCreate() }
    private val locationManager = context.getSystemService(LocationManager::class.java)
    private val registeredIds: MutableSet<String> = Collections.synchronizedSet(mutableSetOf())

    private val observer = object : GeofencingObserver {
        override fun onEntry(event: GeofencingEvent) {
            val id = event.feature.id() ?: return
            _events.tryEmit(GeofenceEvent.Entered(UUID.fromString(id)))
        }
        override fun onDwell(event: GeofencingEvent) = Unit
        override fun onExit(event: GeofencingEvent) {
            val id = event.feature.id() ?: return
            _events.tryEmit(GeofenceEvent.Exited(UUID.fromString(id)))
        }
        override fun onError(error: GeofencingError) { Log.w(TAG, "Geofencing error: $error") }
        override fun onUserConsentChanged(isConsentGiven: Boolean) = Unit
    }

    init {
        geofencing.addObserver(observer) { error -> Log.w(TAG, "addObserver failed: $error") }
    }

    override suspend fun replaceZones(zones: List<GeoFence>) {
        val newIds = zones.map { it.hangoutId.toString() }.toSet()
        val toRemove: Set<String>
        val newZones: List<GeoFence>
        synchronized(registeredIds) {
            toRemove = registeredIds - newIds
            newZones = zones.filter { it.hangoutId.toString() !in registeredIds }
        }

        toRemove.forEach { id ->
            geofencing.removeFeature(id) { error -> Log.w(TAG, "removeFeature $id failed: $error") }
        }

        newZones.forEach { zone ->
            geofencing.addFeature(zone.toFeature()) { error ->
                Log.w(TAG, "addFeature failed: $error")
            }
        }

        emitEnteredForZonesContainingDevice(newZones)

        synchronized(registeredIds) {
            registeredIds.clear()
            registeredIds.addAll(newIds)
        }
    }

    @SuppressLint("MissingPermission")
    private fun emitEnteredForZonesContainingDevice(zones: List<GeoFence>) {
        if (!AppPermission.LOCATION.isGranted(context)) return

        val loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: return
        val devicePoint = Point.fromLngLat(loc.longitude, loc.latitude)
        zones.forEach { zone ->
            val dist = TurfMeasurement.distance(
                devicePoint,
                Point.fromLngLat(zone.longitude, zone.latitude),
                TurfConstants.UNIT_METERS,
            )
            if (dist <= zone.radiusMeters) {
                _events.tryEmit(GeofenceEvent.Entered(zone.hangoutId))
            }
        }
    }

    private fun GeoFence.toFeature(): Feature {
        val circle = TurfTransformation.circle(
            Point.fromLngLat(longitude, latitude),
            radiusMeters,
            64,
            TurfConstants.UNIT_METERS,
        )
        return Feature.fromGeometry(circle, null, hangoutId.toString())
    }

    companion object {
        private const val TAG = "GeofenceRepository"
    }
}
