package be.runeherreman.zuyp.ui.hangout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.graphics.vector.ImageVector
import be.runeherreman.zuyp.data.fake.data.CurrentUser
import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.model.ExpenseShare
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.PersonBalance
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.ui.friends.UserProfile
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class HangoutUiState(
    val hangout: Hangout = Hangout(
        id = UUID.randomUUID(),
        title = "Example hangout",
        description = "This is an example hangout description",
        locationName = "Frankelstraat 28",
        latitude = 50.7,
        longitude = 3.785,
        startDate = LocalDateTime.of(2026, 4, 18, 14, 0),
        endDate = LocalDateTime.of(2026, 4, 18, 23, 0),
        attendees = emptyList(),
        creator = User(UUID.randomUUID(), "KoenK", LocalDate.of(2002, 7, 20), "koen@gmail.com"),
        private = false,
    ),
    val currentUser: User = CurrentUser.user,
    val friendShipMapping: Map<UUID, Boolean> = emptyMap(),
    val weatherPrediction: String = "",
    val isLoadingWeather: Boolean = true,
    val weatherIcon: ImageVector = Icons.Filled.Thermostat,
    val isError: Boolean = false,
    val selectedHangoutId: String? = null,

    // Invites
    val isShareSheetOpen: Boolean = false,
    val allUsers: List<User> = emptyList(),
    val selectedInviteeIds: Set<UUID> = emptySet(),
    val isSendingInvites: Boolean = false,

    // Expenses list
    val expenses: List<Expense> = emptyList(),
    val balances: List<PersonBalance> = emptyList(),
    val selectedExpense: Expense? = null,
    val addExpenseForm: AddExpenseForm? = null,

    val currentUserAttendanceStatus: AttendanceStatus? = null,

    val selectedUserProfile: UserProfile? = null,
)

enum class SplitMode(val label: String) { EQUALLY("Equally"), CUSTOM("Custom") }

sealed interface HangoutEvent {
    data object BackClicked : HangoutEvent
    data object ShareClicked : HangoutEvent
    data object AddExpenseOpen : HangoutEvent
    data object CameraClicked : HangoutEvent
    data object GalleryClicked : HangoutEvent
    data class ExpenseImageCaptured(val path: String) : HangoutEvent
    data object ExpenseDetailClose : HangoutEvent

    data object SendInvites: HangoutEvent
    data object ClearInvitees: HangoutEvent
    data object ShareExternal: HangoutEvent
    data object CloseShare: HangoutEvent

    data class DeleteHangout(val id: UUID) : HangoutEvent
    data class FriendClicked(val userId: UUID) : HangoutEvent
    data class UserClicked(val user: User) : HangoutEvent
    data object UserProfileClose : HangoutEvent
    data class UpdateAttendance(val hangout: Hangout, val status: AttendanceStatus?) : HangoutEvent
    data class ExpenseClicked(val expense: Expense) : HangoutEvent
    data class DeleteExpense(val id: UUID) : HangoutEvent
    data class Settle(val balance: PersonBalance) : HangoutEvent
    data class ToggleInvitee(val id: UUID): HangoutEvent

    // Form events
    data class Form(val event: AddExpenseEvent) : HangoutEvent
}

sealed interface AddExpenseEvent {
    data class TitleChanged(val title: String) : AddExpenseEvent
    data class AmountChanged(val text: String) : AddExpenseEvent
    data class PaidByChanged(val userId: UUID) : AddExpenseEvent
    data class SplitModeChanged(val mode: SplitMode) : AddExpenseEvent
    data class ParticipantToggled(val userId: UUID) : AddExpenseEvent
    data class CustomAmountChanged(val userId: UUID, val text: String) : AddExpenseEvent
    data object ImageRemoved : AddExpenseEvent
    data object Submit : AddExpenseEvent
    data object Dismiss : AddExpenseEvent
}

data class AddExpenseForm(
    val title: String = "",
    val amountText: String = "",
    val paidById: UUID? = null,
    val splitMode: SplitMode = SplitMode.EQUALLY,
    val selectedParticipantIds: Set<UUID> = emptySet(),
    val customAmounts: Map<UUID, String> = emptyMap(),
    val lockedParticipantIds: Set<UUID> = emptySet(), // manually typed → locked; others auto-fill
    val imagePath: String? = null,
    val candidates: List<User> = emptyList(),
    val paidBy: User? = null,
    val participants: List<User> = emptyList(),
    val shares: List<ExpenseShare> = emptyList(),
    val customSum: Double = 0.0,
    val customOk: Boolean = true,
    val canAdd: Boolean = false,
) {
    val amount: Double get() = amountText.replace(',', '.').toDoubleOrNull() ?: 0.0
}