package be.runeherreman.zuyp.ui.hangout

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
import be.runeherreman.zuyp.ui.hangout.components.AddExpenseDialog
import be.runeherreman.zuyp.ui.hangout.components.AttendeesSection
import be.runeherreman.zuyp.ui.hangout.components.BackButton
import be.runeherreman.zuyp.ui.hangout.components.DeleteButton
import be.runeherreman.zuyp.ui.hangout.components.HangoutActionButtons
import be.runeherreman.zuyp.ui.hangout.components.HangoutHeader
import be.runeherreman.zuyp.ui.hangout.components.PrivateBadge
import be.runeherreman.zuyp.ui.hangout.components.ShareHangoutPopup
import be.runeherreman.zuyp.ui.friends.components.UserProfileDialog
import be.runeherreman.zuyp.ui.hangout.components.expenses.ExpenseDetailDialog
import be.runeherreman.zuyp.ui.hangout.components.expenses.ExpensesSection
import java.util.UUID

@Composable
fun HangoutOverlay(
    uiState: HangoutUiState,
    onEvent: (HangoutEvent) -> Unit = {},
) {
    AnimatedVisibility(
        visible = uiState.selectedHangoutId != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HangoutScreen(
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
                uiState = uiState,
                onEvent = onEvent
            )
        }
    }

    if (uiState.isShareSheetOpen) {
        ShareHangoutPopup(
            users = uiState.allUsers,
            selectedIds = uiState.selectedInviteeIds,
            isSending = uiState.isSendingInvites,
            onToggle = { onEvent(HangoutEvent.ToggleInvitee(it)) },
            onInvite = { onEvent(HangoutEvent.SendInvites) },
            onClearSelection = { onEvent(HangoutEvent.ClearInvitees) },
            onShareExternal = { onEvent(HangoutEvent.ShareExternal) },
            onDismiss = { onEvent(HangoutEvent.CloseShare) }
        )
    }
}

@Composable
fun HangoutScreen(
    uiState: HangoutUiState,
    onEvent: (HangoutEvent) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // Handle system back press
    BackHandler(onBack = { onEvent( HangoutEvent.BackClicked) })

    if (uiState.isError) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hangout not found", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .pointerInput(Unit) {}
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            BackButton(onBackClick = { onEvent( HangoutEvent.BackClicked) })
            if (uiState.hangout.creator.id == uiState.currentUser.id) {
                DeleteButton(onDeleteClick = { onEvent(HangoutEvent.DeleteHangout(uiState.hangout.id)) })
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.hangout.private) {
            PrivateBadge()
        }

        HangoutHeader(
            hangout = uiState.hangout,
            uiState = uiState
        )

        Spacer(modifier = Modifier.height(24.dp))

        HangoutActionButtons(
            attendanceStatus = uiState.currentUserAttendanceStatus,
            toggleGoingClick = {
                onEvent(HangoutEvent.UpdateAttendance(uiState.hangout, AttendanceStatus.GOING))
            },
            toggleNotInterestedClick = {
                onEvent(HangoutEvent.UpdateAttendance(uiState.hangout, AttendanceStatus.NOT_INTERESTED))
            },
            onShareClick = { onEvent(HangoutEvent.ShareClicked) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        val goingAttendees = uiState.hangout.attendees.filter {
            it.attendanceStatus == AttendanceStatus.GOING
        }

        AttendeesSection(
            attendees = goingAttendees,
            friendShips = uiState.friendShipMapping,
            currentUserId = uiState.currentUser.id,
            toggleFriendClick = { onEvent(HangoutEvent.FriendClicked(it)) },
            onUserClick = { onEvent(HangoutEvent.UserClicked(it)) }
        )

        if (uiState.currentUserAttendanceStatus == AttendanceStatus.GOING) {
            Spacer(modifier = Modifier.height(32.dp))

            ExpensesSection(
                expenses = uiState.expenses,
                balances = uiState.balances,
                onAddExpense = { onEvent(HangoutEvent.AddExpenseOpen) },
                onExpenseClick = { onEvent(HangoutEvent.ExpenseClicked(it)) },
                onSettle = { onEvent(HangoutEvent.Settle(it)) }
            )

            uiState.addExpenseForm?.let { form ->
                AddExpenseDialog(
                    form = form,
                    currentUser = uiState.currentUser,
                    onEvent = { onEvent(HangoutEvent.Form(it)) },
                    onCameraClick = { onEvent(HangoutEvent.CameraClicked) },
                    onGalleryClick = { onEvent(HangoutEvent.GalleryClicked) }
                )
            }

            uiState.selectedExpense?.let { expense ->
                ExpenseDetailDialog(
                    expense = expense,
                    currentUserId = uiState.currentUser.id,
                    onDelete = { onEvent(HangoutEvent.DeleteExpense(it)) },
                    onDismiss = { onEvent(HangoutEvent.ExpenseDetailClose) }
                )
            }

        }

        uiState.selectedUserProfile?.let { profile ->
            UserProfileDialog(
                profile = profile,
                onDismiss = { onEvent(HangoutEvent.UserProfileClose) },
                onAddFriend = {
                    onEvent(HangoutEvent.FriendClicked(it.id))
                    onEvent(HangoutEvent.UserProfileClose)
                },
                onRemoveFriend = {
                    onEvent(HangoutEvent.FriendClicked(it.id))
                    onEvent(HangoutEvent.UserProfileClose)
                }
            )
        }
    }
}
