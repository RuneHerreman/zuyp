package be.runeherreman.zuyp.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.model.Marker
import be.runeherreman.zuyp.domain.useCases.hangouts.GetHangoutsUseCase
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getHangoutsUseCase: GetHangoutsUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState

    init {
        viewModelScope.launch {
            getHangoutsUseCase().collect { hangouts ->
                _uiState.value = _uiState.value.copy(
                    markers = hangouts.map { hangout ->
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

    fun onUserLocationUpdates(point: Point) {
//        _userlocation.value = point
    }

    fun openHangoutPopup(hangoutId: UUID) {

    }
}