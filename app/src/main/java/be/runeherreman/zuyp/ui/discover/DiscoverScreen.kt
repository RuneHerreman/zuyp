@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package be.runeherreman.zuyp.ui.discover

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.ThemeValue
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import java.util.UUID

@Composable
fun DiscoverScreen(
    uiState: DiscoverUiState,
    onLocationChanged: (Point) -> Unit,
    onMarkerClick: (UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(1.0)
            pitch(0.0)
            bearing(0.0)
        }
    }

    val followPuckOptions = FollowPuckViewportStateOptions.Builder().zoom(12.0).pitch(0.0).build()

    Box(modifier = modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            style = {
                MapboxStandardStyle(
                    standardStyleState = rememberStandardStyleState {
                        configurationsState.apply {
                            theme = ThemeValue.MONOCHROME
                        }
                    }
                )
            }
        ) {
            MapEffect(Unit) { mapView ->
                mapView.location.apply {
                    updateSettings {
                        enabled = true
                        locationPuck = createDefault2DPuck(withBearing = true)
                    }
                    addOnIndicatorPositionChangedListener { point ->
                        onLocationChanged(point)
                    }
                }
                mapViewportState.transitionToFollowPuckState(followPuckOptions)
            }

            val markerIcon = rememberIconImage(
                key = Icons.Filled.LocationOn,
                painter = rememberVectorPainter(image = Icons.Filled.LocationOn)
            )

            uiState.markers.forEach { marker ->
                PointAnnotation(point = marker.position) {
                    iconImage = markerIcon
                    interactionsState.onClicked {
                        onMarkerClick(marker.hangoutId)
                        true
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { mapViewportState.transitionToFollowPuckState(followPuckOptions) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = "My location")
        }
    }
}
