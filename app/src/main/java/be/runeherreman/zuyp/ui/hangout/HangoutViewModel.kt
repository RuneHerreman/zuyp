package be.runeherreman.zuyp.ui.hangout

import android.content.Context
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
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.PersonBalance
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.model.generateWeatherPrediction
import be.runeherreman.zuyp.domain.utils.ExpenseSplitter
import be.runeherreman.zuyp.domain.useCases.expenses.AddExpenseUseCase
import be.runeherreman.zuyp.domain.useCases.expenses.DeleteExpenseUseCase
import be.runeherreman.zuyp.domain.useCases.expenses.GetEventBalancesUseCase
import be.runeherreman.zuyp.domain.useCases.expenses.GetHangoutExpensesUseCase
import be.runeherreman.zuyp.domain.useCases.expenses.SettleDebtUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.AddFriendshipUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.AreFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.GetFriendsUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.DeleteHangoutUseCase
import be.runeherreman.zuyp.domain.useCases.users.GetAllUsersUseCase
import be.runeherreman.zuyp.ui.friends.UserProfile
import be.runeherreman.zuyp.domain.useCases.hangouts.GetHangoutByIdUseCase
import be.runeherreman.zuyp.domain.useCases.api.GetWeatherForecastUseCase
import be.runeherreman.zuyp.domain.useCases.friendship.RemoveFriendshipUseCase
import be.runeherreman.zuyp.domain.useCases.notification.SendHangoutInviteUseCase
import be.runeherreman.zuyp.domain.useCases.hangouts.UpdateAttendanceUseCase
import be.runeherreman.zuyp.domain.useCases.users.DetectShakeUseCase
import be.runeherreman.zuyp.domain.useCases.users.StartShakeDetectionUseCase
import be.runeherreman.zuyp.domain.useCases.users.StopShakeDetectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val settleDebtUseCase: SettleDebtUseCase,
    private val detectShakeUseCase: DetectShakeUseCase,
    private val startShakeDetectionUseCase: StartShakeDetectionUseCase,
    private val stopShakeDetectionUseCase: StopShakeDetectionUseCase,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val expenseSplitter: ExpenseSplitter,
): ViewModel() {
    val currentUser = CurrentUser.user
    private var shakeJob: Job? = null;

    private val _uiState = MutableStateFlow(HangoutUiState())
    val uiState: StateFlow<HangoutUiState> = _uiState

    // =================================================================================
    //                                  INITIAL LOAD
    // =================================================================================
    fun selectHangout(hangoutId: String) {
        _uiState.update { it.copy(selectedHangoutId = hangoutId, isError = false) }
        loadHangoutInfo(hangoutId)
        loadHangoutExpenses(hangoutId)
        listenForShake()
    }

    fun loadHangoutInfo(hangoutId: String) {
        viewModelScope.launch {
            val item = getHangoutByIdUseCase(hangoutId)
            if (item == null) {
                _uiState.update { it.copy(isError = true) }
                return@launch
            }
            _uiState.update { it.copy(hangout = item, currentUserAttendanceStatus = item.attendees.firstOrNull { a -> a.id == currentUser.id }?.attendanceStatus) }
            loadFriendships(item.attendees.map { it.id })
            loadWeatherForHangout(item)
        }
    }

    fun loadHangoutExpenses(hangoutId: String) {
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

    fun loadFriendships(attendeeIds: List<UUID>) {
        val friendshipMap = mutableMapOf<UUID, Boolean>()
        viewModelScope.launch {
            attendeeIds.forEach { attendeeId ->
                friendshipMap[attendeeId] = areFriendsUseCase(currentUser.id, attendeeId)
            }
            _uiState.update { it.copy(friendShipMapping = friendshipMap) }
        }
    }

    fun loadWeatherForHangout(hangout: Hangout) {
        val now = LocalDateTime.now()
        if (hangout.endDate.isBefore(now)) {
            _uiState.update { it.copy(isLoadingWeather = false, weatherPrediction = "No forecast for past events") }
            return
        }

        if (hangout.startDate.isAfter(now.plusDays(14))) {
            _uiState.update { it.copy(isLoadingWeather = false, weatherPrediction = "Forecast only available 14 days in advance") }
            return
        }

        _uiState.update { it.copy(isLoadingWeather = true) }
        viewModelScope.launch {
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
                _uiState.update {
                    it.copy(
                        isLoadingWeather = false,
                        weatherPrediction = "Weather data unavailable"
                    )
                }
            }
        }
    }

    private fun getWeatherIconFromPrediction(weatherPrediction: String) = when {
        weatherPrediction.contains("Heavy rain", ignoreCase = true) -> Icons.Default.Grain
        weatherPrediction.contains("Light rain", ignoreCase = true) -> Icons.Default.Cloud
        else -> Icons.Default.WbSunny
    }




    // =================================================================================
    //                                  EVENT DISTRIBUTION
    // =================================================================================
    fun onEvent(event: HangoutEvent) {
        when (event) {
            HangoutEvent.BackClicked        -> dismissHangout()
            HangoutEvent.ShareClicked       -> openShareSheet()
            HangoutEvent.AddExpenseOpen     -> openAddExpense()
            HangoutEvent.ExpenseDetailClose -> closeExpenseDetail()
            HangoutEvent.SendInvites        -> sendInvites()
            HangoutEvent.ClearInvitees      -> clearInviteeSelection()
            HangoutEvent.CloseShare         -> closeShareSheet()
            is HangoutEvent.DeleteHangout   -> deleteHangout(event.id)
            is HangoutEvent.FriendClicked   -> toggleFriendship(event.userId)
            is HangoutEvent.UserClicked     -> openUserProfile(event.user)
            HangoutEvent.UserProfileClose   -> closeUserProfile()
            is HangoutEvent.UpdateAttendance -> {
                if (_uiState.value.currentUserAttendanceStatus == AttendanceStatus.PRESENT) return
                val next = if (_uiState.value.currentUserAttendanceStatus == event.status) null else event.status
                toggleGoing(event.hangout, next)
            }
            is HangoutEvent.ExpenseClicked  -> openExpenseDetail(event.expense)
            is HangoutEvent.DeleteExpense   -> deleteExpense(event.id)
            is HangoutEvent.Settle          -> settleUp(event.balance)
            is HangoutEvent.ToggleInvitee   -> toggleInvitee(event.id)
            is HangoutEvent.Form            -> onAddExpenseEvent(event.event)

            is HangoutEvent.ExpenseImageCaptured -> updateForm { it.copy(imagePath = event.path) }

            HangoutEvent.CameraClicked,
            HangoutEvent.GalleryClicked,
            HangoutEvent.ShareExternal -> Unit
        }
    }
    fun onAddExpenseEvent(event: AddExpenseEvent) =
        when (event) {
            is AddExpenseEvent.TitleChanged       -> updateForm { it.copy(title = event.title) }
            is AddExpenseEvent.AmountChanged      -> updateForm { it.copy(amountText = event.text) }
            is AddExpenseEvent.PaidByChanged      -> updateForm { form ->
                form.copy(
                    paidById = event.userId,
                    selectedParticipantIds = form.selectedParticipantIds + event.userId
                )
            }
            is AddExpenseEvent.SplitModeChanged   -> updateForm { form ->
                form.copy(
                    splitMode = event.mode,
                    lockedParticipantIds = emptySet(),
                    customAmounts = emptyMap()
                )
            }
            is AddExpenseEvent.ParticipantToggled -> updateForm { form ->
                when (event.userId) {
                    (form.paidById ?: currentUser.id)   -> form
                    in form.selectedParticipantIds      -> form.copy(
                        selectedParticipantIds = form.selectedParticipantIds - event.userId,
                        lockedParticipantIds   = form.lockedParticipantIds   - event.userId
                    )
                    else -> form.copy(
                        selectedParticipantIds = form.selectedParticipantIds + event.userId
                    )
                }
            }
            is AddExpenseEvent.CustomAmountChanged -> updateForm { form ->
                form.copy(
                    customAmounts        = form.customAmounts + (event.userId to event.text),
                    lockedParticipantIds = form.lockedParticipantIds + event.userId
                )
            }
            AddExpenseEvent.ImageRemoved -> updateForm { it.copy(imagePath = null) }
            AddExpenseEvent.Submit       -> submitExpense()
            AddExpenseEvent.Dismiss      -> closeAddExpense()
        }





    // =================================================================================
    //                                      HANGOUTS
    // =================================================================================
    fun dismissHangout() = viewModelScope.launch {
        stopListeningForShake()
        _uiState.update { it.copy(selectedHangoutId = null, isError = false) }
    }

    fun openUserProfile(user: User) {
        viewModelScope.launch {
            val theirFriends  = getFriendsUseCase(user.id)
            val myFriendIds   = getFriendsUseCase(currentUser.id).mapTo(mutableSetOf()) { it.id }
            val mutualFriends = theirFriends.filter { it.id in myFriendIds && it.id != currentUser.id }
            val isFriend      = user.id in myFriendIds
            _uiState.update {
                it.copy(
                    selectedUserProfile = UserProfile(
                        user          = user,
                        friendsCount  = theirFriends.size,
                        groupsCount   = 0,
                        eventsCount   = 0,
                        mutualFriends = mutualFriends,
                        isFriend      = isFriend
                    )
                )
            }
        }
    }

    fun closeUserProfile() = _uiState.update { it.copy(selectedUserProfile = null) }

    fun deleteHangout(hangoutId: UUID) {
        viewModelScope.launch {
            deleteHangoutUseCase(hangoutId, _uiState.value.currentUser.id)
            dismissHangout()
        }
    }





    // =================================================================================
    //                             FRIENDSHIPS AND ATTENDANCE
    // =================================================================================
    fun toggleFriendship(targetUserId: UUID) {
        viewModelScope.launch {
            val currentFriendshipStatus = _uiState.value.friendShipMapping[targetUserId] ?: false
            try {
                if (currentFriendshipStatus) {
                    removeFriendshipUseCase(currentUser.id, targetUserId)
                    _uiState.update { it.copy(friendShipMapping = it.friendShipMapping + (targetUserId to false)) }
                    Log.i("HangoutViewModel", "Friendship removed")
                } else {
                    addFriendshipUseCase(currentUser.id, targetUserId)
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
                    _uiState.update { it.copy(hangout = updatedHangout, currentUserAttendanceStatus = updatedHangout.attendees.firstOrNull { a -> a.id == currentUser.id }?.attendanceStatus) }
                    Log.i("HangoutViewModel", "Attendance toggled")
                }
            } catch (e: Exception) {
                Log.e("HangoutViewModel", "Error toggling going", e)
            }
        }
    }




    // =================================================================================
    //                                      INVITES
    // =================================================================================
    fun openShareSheet() {
        _uiState.update { it.copy(isShareSheetOpen = true) }
        viewModelScope.launch {
            val attendeeIds = _uiState.value.hangout.attendees.map { it.id }.toSet()
            val allUsers = getAllUsersUseCase().filter {
                it.id != currentUser.id && it.id !in attendeeIds
            }
            _uiState.update { it.copy(allUsers = allUsers) }
        }
    }

    fun closeShareSheet() {
        _uiState.update { it.copy(isShareSheetOpen = false, selectedInviteeIds = emptySet()) }
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

    fun clearInviteeSelection() {
        _uiState.update { it.copy(selectedInviteeIds = emptySet()) }
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

    fun shareHangoutExternally(hangout: Hangout, context: Context) {
        val shareLink = "https://runeherreman.github.io/zuyp/hangout/${hangout.id}" // redirect via github pages
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

    // =================================================================================
    //                                      EXPENSES
    // =================================================================================
    private fun expenseCandidates(state: HangoutUiState): List<User> =
        (listOf(state.currentUser) + state.hangout.attendees).distinctBy { it.id }

    private fun loadExpenseForm(form: AddExpenseForm, candidates: List<User>): AddExpenseForm {
        val paidBy = resolvePaidBy(form.paidById, candidates)
        val participants = candidates.filter { it.id in form.selectedParticipantIds }

        // Auto-fill unlocked participants so the split always sums to the total
        val resolvedAmounts = if (form.splitMode == SplitMode.CUSTOM) {
            expenseSplitter.autoFillAmounts(
                participants = participants,
                amount = form.amount,
                lockedIds = form.lockedParticipantIds,
                customAmounts = form.customAmounts,
            )
        } else {
            form.customAmounts
        }

        val shares = when (form.splitMode) {
            SplitMode.EQUALLY -> expenseSplitter.equalShares(participants, form.amount, paidBy)
            SplitMode.CUSTOM  -> expenseSplitter.customShares(participants, resolvedAmounts)
        }
        val customSum = shares.sumOf { it.amount }
        val customOk  = form.splitMode != SplitMode.CUSTOM || expenseSplitter.sharesMatchTotal(shares, form.amount)
        val canAdd    = form.title.isNotBlank() && form.amount > 0.0 && participants.isNotEmpty() && customOk
        return form.copy(
            customAmounts = resolvedAmounts,
            candidates = candidates,
            paidBy     = paidBy,
            participants = participants,
            shares     = shares,
            customSum  = customSum,
            customOk   = customOk,
            canAdd     = canAdd,
        )
    }

    private fun resolvePaidBy(paidById: UUID?, candidates: List<User>): User = candidates.firstOrNull { it.id == paidById } ?: currentUser

    private fun updateForm(update: (AddExpenseForm) -> AddExpenseForm) {
        _uiState.update { state ->
            val form = state.addExpenseForm ?: return@update state
            state.copy(addExpenseForm = loadExpenseForm(update(form), expenseCandidates(state)))
        }
    }

    fun openAddExpense() {
        val state = _uiState.value
        val form = loadExpenseForm(
            AddExpenseForm(
                paidById = currentUser.id,
                selectedParticipantIds = setOf(currentUser.id),
            ),
            expenseCandidates(state)
        )
        _uiState.update { it.copy(addExpenseForm = form) }
    }

    fun openExpenseDetail(expense: Expense) = _uiState.update { it.copy(selectedExpense = expense) }

    private fun closeAddExpense() = _uiState.update { it.copy(addExpenseForm = null) }

    fun closeExpenseDetail() = _uiState.update { it.copy(selectedExpense = null) }

    fun settleUp(person: PersonBalance) = viewModelScope.launch {
        if (person.net < 0) settleDebtUseCase(UUID.fromString(_uiState.value.selectedHangoutId), currentUser.id, person.user.id, -person.net)
    }

    private fun submitExpense() {
        val state = _uiState.value
        val form = state.addExpenseForm ?: return
        if (!form.canAdd) return
        viewModelScope.launch {
            addExpenseUseCase(
                Expense(
                    id = UUID.randomUUID(),
                    hangoutId = UUID.fromString(state.selectedHangoutId),
                    title = form.title.trim(),
                    amount = form.amount,
                    paidBy = form.paidBy ?: currentUser,
                    imageUri = form.imagePath,
                    createdAt = LocalDateTime.now(),
                    shares = form.shares
                )
            )
            _uiState.update { it.copy(addExpenseForm = null) }
        }
    }

    fun deleteExpense(id: UUID) = viewModelScope.launch {
        deleteExpenseUseCase(id, currentUser.id)
        _uiState.update { it.copy(selectedExpense = null) }
    }


    // SHAKE SENSOR
    private fun listenForShake() {
        shakeJob?.cancel()
        startShakeDetectionUseCase()

        shakeJob = viewModelScope.launch {
            detectShakeUseCase().collect {
                val status = _uiState.value.currentUserAttendanceStatus
                if (status != AttendanceStatus.GOING && status != AttendanceStatus.PRESENT) {
                    toggleGoing(_uiState.value.hangout, AttendanceStatus.GOING)
                }
            }
        }
    }

    // =================================================================================
    //                                  SHAKE SENSOR
    // =================================================================================
    private fun stopListeningForShake() {
        shakeJob?.cancel()
        shakeJob = null
        stopShakeDetectionUseCase()
    }

    override fun onCleared() {
        super.onCleared()
        stopListeningForShake()
    }
}
