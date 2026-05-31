package be.runeherreman.zuyp.ui.hangout

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.WbSunny
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.Weather
import be.runeherreman.zuyp.domain.useCases.AddFriendshipUseCase
import be.runeherreman.zuyp.domain.useCases.AreFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.GetHangoutByIdUseCase
import be.runeherreman.zuyp.domain.useCases.GetWeatherForecastUseCase
import be.runeherreman.zuyp.domain.useCases.RemoveFriendshipUseCase
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
    private val getWeatherUseCase: GetWeatherForecastUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(HangoutUiState())
    val uiState: StateFlow<HangoutUiState> = _uiState

    fun loadHangout(hangoutId: String) {
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
            val weatherString = generateWeatherString(weather, hangout)
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

    private fun generateWeatherString(weather: Weather, hangout: Hangout): String {
        if (weather.hourly.temperature2m.isEmpty()) return "Weather data unavailable"

        val now = java.time.LocalDateTime.now()
        val startTime = hangout.startDate
        val hourIndex = if (startTime.isBefore(now)) {
            weather.hourly.temperature2m.size - 1
        } else {
            val hoursDiff = java.time.temporal.ChronoUnit.HOURS.between(now, startTime).toInt()
            minOf(hoursDiff, weather.hourly.temperature2m.size - 1).coerceAtLeast(0)
        }

        val temperature = weather.hourly.temperature2m.getOrNull(hourIndex)?.toInt() ?: 0
        val rain = weather.hourly.rain.getOrNull(hourIndex) ?: 0.0

        val weatherStatus = when {
            rain > 5.0 -> "Heavy rain"
            rain > 1.0 -> "Light rain"
            else -> "Clear skies"
        }

        val clothingRecommendation = when {
            rain > 5.0 -> "Wear rain coat"
            rain > 1.0 && temperature < 15 -> "Wear sweater, rain protection"
            rain > 1.0 && temperature >= 20 -> "T-shirt w/ light rain jacket"
            rain > 1.0 -> "Bring umbrella / light rain jacket"
            temperature < 7 -> "Dress warmly w/ winter jacket"
            temperature < 15 -> "Wear a sweater"
            temperature < 22 -> "Wear a light jacket"
            else -> "T-shirt and shorts"
        }

        return "$temperature°C - $weatherStatus - $clothingRecommendation"
    }

    private fun getWeatherIconFromPrediction(weatherPrediction: String) = when {
        weatherPrediction.contains("Heavy rain", ignoreCase = true) -> Icons.Default.Grain
        weatherPrediction.contains("Light rain", ignoreCase = true) -> Icons.Default.Cloud
        else -> Icons.Default.WbSunny
    }
}
