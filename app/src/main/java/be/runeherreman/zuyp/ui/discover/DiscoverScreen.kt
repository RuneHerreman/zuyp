@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package be.runeherreman.zuyp.ui.discover

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import be.runeherreman.zuyp.domain.model.Marker
import com.mapbox.geojson.Point
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.ThemeValue
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions

@Composable
fun DiscoverScreen(
    uiState: DiscoverUiState,
    onLocationChanged: (Point) -> Unit,
    onMarkerClick: (Marker) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val followPuckOptions = FollowPuckViewportStateOptions.Builder().zoom(12.0).pitch(0.0).build()

    Box(modifier = modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = uiState.viewportState,
            onMapClickListener = {
                onMapClick()
                true
            },
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
                uiState.viewportState.transitionToFollowPuckState(followPuckOptions)
            }

            uiState.markers.forEach { marker ->
                val isSelected = uiState.selectedHangout?.id == marker.hangoutId && uiState.hangoutPopupOpen

                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(marker.position)

                    },
                ) {
                    val animatedSize by animateFloatAsState(
                        targetValue = if (isSelected) 51f else 32f,
                        label = "markerSize"
                    )

                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(animatedSize.dp)
                            .clickable { onMarkerClick(marker) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { uiState.viewportState.transitionToFollowPuckState(followPuckOptions) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = "My location")
        }
    }
}
