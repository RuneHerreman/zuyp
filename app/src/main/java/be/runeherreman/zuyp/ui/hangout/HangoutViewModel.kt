package be.runeherreman.zuyp.ui.hangout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            getHangoutByIdUseCase(hangoutId).let { item ->
                _uiState.update {
                    it.copy(
                        hangout = item ?: HangoutUiState().hangout
                    )
                }
                // Load friendships and weather
                loadFriendships(item?.attendees?.map { attendee -> attendee.id } ?: emptyList())
                loadWeather(item?.latitude ?: 0.0, item?.longitude ?: 0.0)
            }
        }
    }

    fun loadFriendships(attendeeIds: List<UUID>) {
        viewModelScope.launch {
            val currentUserId = _uiState.value.currentUser.id
            val friendshipMap = mutableMapOf<UUID, Boolean>()
            
            attendeeIds.forEach { attendeeId ->
                val isFriend = areFriendsUseCase(currentUserId, attendeeId)
                friendshipMap[attendeeId] = isFriend
            }
            
            _uiState.update {
                it.copy(
                    friendShipMapping = friendshipMap
                )
            }
        }
    }

    fun loadWeather(
        latitude: Double,
        longitude: Double,
        hourly: String = "temperature_2m,rain",
        timezone: String = "auto",
        startDate: String = "2022-01-01",
        endDate: String = "2022-12-31"
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingWeather = true) }
            try {
                val weather = getWeatherUseCase(
                    latitude = latitude,
                    longitude = longitude,
                    hourly = hourly,
                    timezone = timezone,
                    startDate = startDate,
                    endDate = endDate
                )

                _uiState.update {
                    it.copy(
                        weatherPrediction = generateWeatherString(weather),
                        isLoadingWeather = false
                    )
                }
                Log.i("HangoutViewModel - Weather", "Weather loaded: ${weather.hourly.temperature_2m.min()}, - ${weather.hourly.rain.max()}")
            } catch (e: Exception) {
                Log.e("HangoutViewModel - Weather", "Error loading weather", e)
                _uiState.update { it.copy(isLoadingWeather = false) }
            }
        }
    }

    fun toggleFriendship(targetUserId: UUID) {
        viewModelScope.launch {
            val currentUserId = _uiState.value.currentUser.id
            val currentFriendshipStatus = _uiState.value.friendShipMapping[targetUserId] ?: false
            
            try {
                if (currentFriendshipStatus) { // Remove
                    removeFriendshipUseCase(currentUserId, targetUserId)
                    _uiState.update {
                        it.copy(
                            friendShipMapping = it.friendShipMapping + (targetUserId to false)
                        )
                    }
                    Log.i("HangoutViewModel - Friendships", "Friendship removed")
                } else { // Add
                    addFriendshipUseCase(currentUserId, targetUserId)
                    _uiState.update {
                        it.copy(
                            friendShipMapping = it.friendShipMapping + (targetUserId to true)
                        )
                    }
                    Log.i("HangoutViewModel - Friendships", "Friendship added")
                }
            } catch (e: Exception) {
                Log.e("HangoutViewModel - Friendships", "Error toggling friendship", e)
            }
        }
    }

    fun generateWeatherString(weather: Weather): String {
        return ""
    }
}