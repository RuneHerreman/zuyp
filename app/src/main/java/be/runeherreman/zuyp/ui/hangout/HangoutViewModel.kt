package be.runeherreman.zuyp.ui.hangout

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.WbSunny
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.local.room.entity.AttendanceStatus
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.Weather
import be.runeherreman.zuyp.domain.model.generateWeatherPrediction
import be.runeherreman.zuyp.domain.useCases.AddFriendshipUseCase
import be.runeherreman.zuyp.domain.useCases.AreFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.GetHangoutByIdUseCase
import be.runeherreman.zuyp.domain.useCases.GetWeatherForecastUseCase
import be.runeherreman.zuyp.domain.useCases.RemoveFriendshipUseCase
import be.runeherreman.zuyp.domain.useCases.UpdateAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HangoutViewModel @Inject constructor(
    private val getHangoutByIdUseCase: GetHangoutByIdUseCase,
    private val areFriendsUseCase: AreFriendsUseCase,
    private val addFriendshipUseCase: AddFriendshipUseCase,
    private val removeFriendshipUseCase: RemoveFriendshipUseCase,
    private val getWeatherUseCase: GetWeatherForecastUseCase,
    private val updateAttendanceUseCase: UpdateAttendanceUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(HangoutUiState())
    val uiState: StateFlow<HangoutUiState> = _uiState

    fun selectHangout(hangoutId: String) {
        _uiState.update { it.copy(selectedHangoutId = hangoutId) }
        viewModelScope.launch {
            val item = getHangoutByIdUseCase(hangoutId)
            if (item == null) {
                _uiState.update { it.copy(isError = true) }
                return@launch
            }
            _uiState.update { it.copy(hangout = item) }
            loadFriendships(item.attendees.map { it.id })
            loadWeatherForHangout(item)
        }
    }

    fun dismissHangout() {
        _uiState.update { it.copy(selectedHangoutId = null, isError = false) }
    }

    private suspend fun loadFriendships(attendeeIds: List<UUID>) {
        val currentUserId = _uiState.value.currentUser.id
        val friendshipMap = mutableMapOf<UUID, Boolean>()
        attendeeIds.forEach { attendeeId ->
            friendshipMap[attendeeId] = areFriendsUseCase(currentUserId, attendeeId)
        }
        _uiState.update { it.copy(friendShipMapping = friendshipMap) }
    }

    private suspend fun loadWeatherForHangout(hangout: Hangout) {
        if (hangout.endDate.isBefore(java.time.LocalDateTime.now())) {
            _uiState.update { it.copy(isLoadingWeather = false, weatherPrediction = "No forecast for past events") }
            return
        }
        _uiState.update { it.copy(isLoadingWeather = true) }
        try {
            val weather = getWeatherUseCase(
                latitude = hangout.latitude,
                longitude = hangout.longitude,
                hourly = "temperature_2m,rain",
                timezone = "auto",
                startDate = hangout.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                endDate = hangout.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )
            val weatherString = generateWeatherPrediction(weather, hangout)
            val icon = getWeatherIconFromPrediction(weatherString)
            _uiState.update {
                it.copy(
                    weatherPrediction = weatherString,
                    weatherIcon = icon,
                    isLoadingWeather = false
                )
            }
            Log.i("HangoutViewModel", "Weather: ${weather.hourly.temperature2m.min()}°C, rain: ${weather.hourly.rain.max()}mm")
        } catch (e: Exception) {
            Log.e("HangoutViewModel", "Error loading weather", e)
            _uiState.update { it.copy(isLoadingWeather = false) }
        }
    }

    fun toggleFriendship(targetUserId: UUID) {
        viewModelScope.launch {
            val currentUserId = _uiState.value.currentUser.id
            val currentFriendshipStatus = _uiState.value.friendShipMapping[targetUserId] ?: false
            try {
                if (currentFriendshipStatus) {
                    removeFriendshipUseCase(currentUserId, targetUserId)
                    _uiState.update { it.copy(friendShipMapping = it.friendShipMapping + (targetUserId to false)) }
                    Log.i("HangoutViewModel", "Friendship removed")
                } else {
                    addFriendshipUseCase(currentUserId, targetUserId)
                    _uiState.update { it.copy(friendShipMapping = it.friendShipMapping + (targetUserId to true)) }
                    Log.i("HangoutViewModel", "Friendship added")
                }
            } catch (e: Exception) {
                Log.e("HangoutViewModel", "Error toggling friendship", e)
            }
        }
    }

    fun toggleGoing(hangout: Hangout, attendanceStatus: AttendanceStatus? = null) {
        viewModelScope.launch {
            try {
                updateAttendanceUseCase(
                    hangoutId = hangout.id,
                    userId = _uiState.value.currentUser.id,
                    attendaceStatus = attendanceStatus
                )
                // Reload the hangout to reflect the changes
                val updatedHangout = getHangoutByIdUseCase(hangout.id.toString())
                if (updatedHangout != null) {
                    _uiState.update { it.copy(hangout = updatedHangout) }
                    Log.i("HangoutViewModel", "Attendance toggled")
                }
            } catch (e: Exception) {
                Log.e("HangoutViewModel", "Error toggling going", e)
            }
        }
    }

    private fun getWeatherIconFromPrediction(weatherPrediction: String) = when {
        weatherPrediction.contains("Heavy rain", ignoreCase = true) -> Icons.Default.Grain
        weatherPrediction.contains("Light rain", ignoreCase = true) -> Icons.Default.Cloud
        else -> Icons.Default.WbSunny
    }
}
