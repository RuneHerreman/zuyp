package be.runeherreman.zuyp.ui.hangout

import android.content.Intent
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.WbSunny
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.model.ExpenseShare
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.PersonBalance
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.model.generateWeatherPrediction
import be.runeherreman.zuyp.domain.useCases.expenses.AddExpenseUseCase
import be.runeherreman.zuyp.domain.useCases.expenses.DeleteExpenseUseCase
import be.runeherreman.zuyp.domain.useCases.expenses.GetEventBalancesUseCase
import be.runeherreman.zuyp.domain.useCases.expenses.GetHangoutExpensesUseCase
import be.runeherreman.zuyp.domain.useCases.expenses.SettleDebtUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.AddFriendshipUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.AreFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.DeleteHangoutUseCase
import be.runeherreman.zuyp.domain.useCases.users.GetAllUsersUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.GetHangoutByIdUseCase
import be.runeherreman.zuyp.domain.useCases.utils.GetWeatherForecastUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.RemoveFriendshipUseCase
import be.runeherreman.zuyp.domain.useCases.notification.SendHangoutInviteUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.UpdateAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
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
    private val updateAttendanceUseCase: UpdateAttendanceUseCase,
    private val deleteHangoutUseCase: DeleteHangoutUseCase,
    private val sendHangoutInviteUseCase: SendHangoutInviteUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getHangoutExpensesUseCase: GetHangoutExpensesUseCase,
    private val getEventBalancesUseCase: GetEventBalancesUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val settleDebtUseCase: SettleDebtUseCase
): ViewModel() {
    val currentUser = CurrentUser.user
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
        viewModelScope.launch {
            combine(
                getHangoutExpensesUseCase(UUID.fromString(hangoutId)),
                getEventBalancesUseCase(UUID.fromString(hangoutId), currentUser.id)
            ) { expenses, balances -> expenses to balances }
                .collect { (expenses, balances) ->
                    _uiState.update { it.copy(expenses = expenses, balances = balances) }
                }
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

    fun deleteHangout(hangoutId: UUID) {
        viewModelScope.launch {
            deleteHangoutUseCase(hangoutId, _uiState.value.currentUser.id)
            dismissHangout()
        }
    }

    private fun getWeatherIconFromPrediction(weatherPrediction: String) = when {
        weatherPrediction.contains("Heavy rain", ignoreCase = true) -> Icons.Default.Grain
        weatherPrediction.contains("Light rain", ignoreCase = true) -> Icons.Default.Cloud
        else -> Icons.Default.WbSunny
    }

    // ==============
    // INVITES
    // ===============
    fun openShareSheet() {
        _uiState.update { it.copy(isShareSheetOpen = true) }
        viewModelScope.launch {
            val currentUserId = _uiState.value.currentUser.id
            val attendeeIds = _uiState.value.hangout.attendees.map { it.id }.toSet()
            val allUsers = getAllUsersUseCase().filter {
                it.id != currentUserId && it.id !in attendeeIds
            }
            _uiState.update { it.copy(allUsers = allUsers) }
        }
    }

    fun closeShareSheet() {
        _uiState.update { it.copy(isShareSheetOpen = false, selectedInviteeIds = emptySet()) }
    }

    fun clearInviteeSelection() {
        _uiState.update { it.copy(selectedInviteeIds = emptySet()) }
    }

    fun toggleInvitee(userId: UUID) {
        _uiState.update {
            val newSelection = if (userId in it.selectedInviteeIds) {
                it.selectedInviteeIds - userId
            } else {
                it.selectedInviteeIds + userId
            }
            it.copy(selectedInviteeIds = newSelection)
        }
    }

    fun sendInvites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingInvites = true) }
            _uiState.value.selectedInviteeIds.forEach { inviteeId ->
                sendHangoutInviteUseCase(
                    inviteeId,
                    _uiState.value.hangout.id
                )
            }
            _uiState.update { it.copy(isSendingInvites = false) }
            closeShareSheet()
        }
    }

    fun shareHangoutExternally(hangout: Hangout, context: android.content.Context) {
        // Clickable https link (GitHub Pages) that redirects to the zuyp:// deep link.
        val shareLink = "https://runeherreman.github.io/zuyp/hangout/${hangout.id}"
        val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d 'at' HH:mm")
        val text = buildString {
            append("Join me at \"${hangout.title}\"!\n")
            append("📍 ${hangout.locationName}\n")
            append("${hangout.startDate.format(dateTimeFormatter)} - ${hangout.endDate.format(dateTimeFormatter)}\n")
            append(shareLink)
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, hangout.title)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share hangout"))
    }

    // EXPENSES FUNCTIONSS
    fun openAddExpense() { _uiState.update { it.copy(isAddExpenseOpen = true) } }
    fun addExpense(title: String, amount: Double, paidBy: User, shares: List<ExpenseShare>, imageUri: String?) {
        viewModelScope.launch {
            addExpenseUseCase(
                Expense(
                    UUID.randomUUID(),
                    UUID.fromString(_uiState.value.selectedHangoutId),
                    title,
                    amount,
                    paidBy,
                    imageUri,
                    LocalDateTime.now(),
                    shares
                )
            )
            _uiState.update { it.copy(isAddExpenseOpen = false) }
        }
    }
    fun deleteExpense(id: UUID) = viewModelScope.launch { deleteExpenseUseCase(id, currentUser.id) ; _uiState.update { it.copy(selectedExpense = null) } }
    fun settleUp(person: PersonBalance) = viewModelScope.launch {
        if (person.net < 0) settleDebtUseCase(UUID.fromString(_uiState.value.selectedHangoutId), currentUser.id, person.user.id, -person.net)
    }
}
