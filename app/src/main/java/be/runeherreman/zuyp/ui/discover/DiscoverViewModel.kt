package be.runeherreman.zuyp.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.Marker
import be.runeherreman.zuyp.domain.useCases.hangouts.GetHangoutByIdUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.GetHangoutsUseCase
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getHangoutsUseCase: GetHangoutsUseCase,
    private val getHangoutByIdUseCase: GetHangoutByIdUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState

    init {
        _uiState.value.viewportState.setCameraOptions {
            zoom(1.0)
            pitch(0.0)
            bearing(0.0)
        }

        viewModelScope.launch {
            getHangoutsUseCase().collect { hangouts ->
                _uiState.update { state ->
                    state.copy(
                        markers = hangouts.filter(::isInTimeRange).map { hangout ->
                            Marker(
                                hangoutId = hangout.id,
                                title = hangout.title,
                                position = Point.fromLngLat(hangout.longitude, hangout.latitude)
                            )
                        }
                    )
                }
            }
        }
    }

    fun isInTimeRange(hangout: Hangout): Boolean {
        return  hangout.startDate.isBefore(LocalDateTime.now().plusDays(30)) ||
                hangout.startDate.isAfter(LocalDateTime.now().minusHours(12))
    }

    fun onUserLocationUpdates(point: Point) {
//        _userlocation.value = point
    }

    fun openHangoutPopup(marker: Marker) {
        // Use flyTo for a smooth animated transition
        _uiState.value.viewportState.flyTo(
            CameraOptions.Builder()
                .center(marker.position)
                .zoom(14.0)
                .build(),
            MapAnimationOptions.mapAnimationOptions { duration(1000) }
        )

        viewModelScope.launch {
            val hangout = getHangoutByIdUseCase(marker.hangoutId.toString()) ?: return@launch
            _uiState.update { it.copy(selectedHangout = hangout, hangoutPopupOpen = true) }
        }
    }

    fun closeHangoutPopup() {
        _uiState.update { it.copy(hangoutPopupOpen = false) }
    }
}
