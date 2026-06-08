package be.runeherreman.zuyp.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.Marker
import be.runeherreman.zuyp.domain.usecases.hangouts.GetAllHangoutsUseCase
import be.runeherreman.zuyp.domain.usecases.hangouts.GetHangoutByIdUseCase
import be.runeherreman.zuyp.domain.usecases.hangouts.UpdateAttendanceUseCase
import be.runeherreman.zuyp.domain.usecases.users.DetectShakeUseCase
import be.runeherreman.zuyp.domain.usecases.users.StartShakeDetectionUseCase
import be.runeherreman.zuyp.domain.usecases.users.StopShakeDetectionUseCase
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getAllHangoutsUseCase: GetAllHangoutsUseCase,
    private val getHangoutByIdUseCase: GetHangoutByIdUseCase,
    private val updateAttendanceUseCase: UpdateAttendanceUseCase,
    private val detectShakeUseCase: DetectShakeUseCase,
    private val startShakeDetectionUseCase: StartShakeDetectionUseCase,
    private val stopShakeDetectionUseCase: StopShakeDetectionUseCase,
): ViewModel() {
    private val currentUser = CurrentUser.user
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState

    private var shakeJob: Job? = null

    init {
        _uiState.value.viewportState.setCameraOptions {
            zoom(1.0)
            pitch(0.0)
            bearing(0.0)
        }

        viewModelScope.launch {
            val ticker = flow {
                while (true) {
                    emit(Unit)
                    delay(TIME_FILTER_REFRESH_MS)
                }
            }
            combine(getAllHangoutsUseCase(), ticker) { hangouts, _ -> hangouts }
                .collect { hangouts ->
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
        return hangout.endDate.isAfter(LocalDateTime.now().minusHours(12)) &&
               hangout.startDate.isBefore(LocalDateTime.now().plusDays(30))
    }

//    fun onUserLocationUpdates(point: Point) {
//        _userlocation.value = point
//    }

    fun openHangoutPopup(marker: Marker) {
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
            listenForShake()
        }
    }

    fun closeHangoutPopup() {
        stopListeningForShake()
        _uiState.update { it.copy(hangoutPopupOpen = false) }
    }

    private fun listenForShake() {
        shakeJob?.cancel()
        startShakeDetectionUseCase()
        shakeJob = viewModelScope.launch {
            detectShakeUseCase().collect {
                val current = _uiState.value.selectedHangout?.attendees
                    ?.firstOrNull { it.id == currentUser.id }
                    ?.attendanceStatus
                if (current != AttendanceStatus.GOING && current != AttendanceStatus.PRESENT) {
                    toggleAttendance(AttendanceStatus.GOING)
                }
            }
        }
    }

    private fun stopListeningForShake() {
        shakeJob?.cancel()
        shakeJob = null
        stopShakeDetectionUseCase()
    }

    override fun onCleared() {
        super.onCleared()
        stopListeningForShake()
    }


    fun showBackgroundLocationRationale() = _uiState.update { it.copy(showBackgroundLocationDialog = true) }
    fun dismissBackgroundLocationRationale() = _uiState.update { it.copy(showBackgroundLocationDialog = false) }

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

    companion object {
        private const val TIME_FILTER_REFRESH_MS = 120_000L
    }
}
