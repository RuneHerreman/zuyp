package be.runeherreman.zuyp.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.Marker
import be.runeherreman.zuyp.domain.useCases.hangouts.GetAllHangoutsUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.GetHangoutByIdUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.UpdateAttendanceUseCase
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
    private val getAllHangoutsUseCase: GetAllHangoutsUseCase,
    private val getHangoutByIdUseCase: GetHangoutByIdUseCase,
    private val updateAttendanceUseCase: UpdateAttendanceUseCase
): ViewModel() {
    private val currentUser = CurrentUser.user
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState

    init {
        _uiState.value.viewportState.setCameraOptions {
            zoom(1.0)
            pitch(0.0)
            bearing(0.0)
        }

        viewModelScope.launch {
            getAllHangoutsUseCase().collect { hangouts ->
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
        return hangout.startDate.isAfter(LocalDateTime.now().minusHours(12)) &&
               hangout.startDate.isBefore(LocalDateTime.now().plusDays(30))
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

    fun showBackgroundLocationRationale() {
        _uiState.update { it.copy(showBackgroundLocationRationale = true) }
    }

    fun dismissBackgroundLocationRationale() {
        _uiState.update { it.copy(showBackgroundLocationRationale = false) }
    }

    /**
     * Toggles the current user's attendance for the selected hangout. Tapping the
     * status the user already holds clears it (mirrors HangoutViewModel.toggleGoing).
     */
    fun toggleAttendance(target: AttendanceStatus) {
        val hangout = _uiState.value.selectedHangout ?: return
        val current = hangout.attendees
            .firstOrNull { it.id == currentUser.id }
            ?.attendanceStatus
        val next = if (current == target) null else target

        viewModelScope.launch {
            updateAttendanceUseCase(
                hangoutId = hangout.id,
                userId = currentUser.id,
                attendaceStatus = next
            )
            val updated = getHangoutByIdUseCase(hangout.id.toString()) ?: return@launch
            _uiState.update { it.copy(selectedHangout = updated) }
        }
    }
}
