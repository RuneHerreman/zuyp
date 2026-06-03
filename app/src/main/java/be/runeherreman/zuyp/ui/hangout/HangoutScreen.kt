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
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.ui.hangout.components.HangoutActionButtons
import be.runeherreman.zuyp.ui.hangout.components.AttendeesSection
import be.runeherreman.zuyp.ui.hangout.components.BackButton
import be.runeherreman.zuyp.ui.hangout.components.DeleteButton
import be.runeherreman.zuyp.ui.hangout.components.ExpensesSection
import be.runeherreman.zuyp.ui.hangout.components.HangoutHeader
import be.runeherreman.zuyp.ui.hangout.components.PrivateBadge
import be.runeherreman.zuyp.ui.hangout.components.ShareHangoutPopup
import java.util.UUID

@Composable
fun HangoutOverlay(
    uiState: HangoutUiState,
    onDismiss: () -> Unit,
    onFriendClick: (UUID) -> Unit,
    onDeleteClick: (UUID) -> Unit,
    onUpdateAttendanceStatus: (Hangout, AttendanceStatus?) -> Unit,
    onShareClick: () -> Unit = {},
    onToggleInvitee: (UUID) -> Unit = {},
    onSendInvites: () -> Unit = {},
    onClearInvitees: () -> Unit = {},
    onShareExternal: () -> Unit = {},
    onCloseShare: () -> Unit = {},
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
                onBackClick = onDismiss,
                onFriendClick = onFriendClick,
                onUpdateAttendanceStatus = onUpdateAttendanceStatus,
                onDeleteClick = onDeleteClick,
                onShareClick = onShareClick
            )
        }
    }

    if (uiState.isShareSheetOpen) {
        ShareHangoutPopup(
            users = uiState.allUsers,
            selectedIds = uiState.selectedInviteeIds,
            isSending = uiState.isSendingInvites,
            onToggle = onToggleInvitee,
            onInvite = onSendInvites,
            onClearSelection = onClearInvitees,
            onShareExternal = onShareExternal,
            onDismiss = onCloseShare
        )
    }
}

@Composable
fun HangoutScreen(
    uiState: HangoutUiState,
    onBackClick: () -> Unit = {},
    onDeleteClick: (UUID) -> Unit = {},
    onFriendClick: (UUID) -> Unit = {},
    onUpdateAttendanceStatus: (Hangout, AttendanceStatus?) -> Unit = {_, _, ->},
    onShareClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // Handle system back press
    BackHandler(onBack = onBackClick)

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
            BackButton(onBackClick = onBackClick)
            if (uiState.hangout.creator.id == uiState.currentUser.id) {
                DeleteButton(onDeleteClick = { onDeleteClick(uiState.hangout.id) })
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
            attendanceStatus = uiState.currentUserAttendanceStatus(),
            toggleGoingClick = {
                onUpdateAttendanceStatus(uiState.hangout, uiState.nextAttendanceStatus(AttendanceStatus.GOING))
            },
            toggleNotInterestedClick = {
                onUpdateAttendanceStatus(uiState.hangout, uiState.nextAttendanceStatus(AttendanceStatus.NOT_INTERESTED))
            },
            onShareClick = onShareClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        val goingAttendees = uiState.hangout.attendees.filter {
            it.attendanceStatus == AttendanceStatus.GOING
        }

        AttendeesSection(
            attendees = goingAttendees,
            friendShips = uiState.friendShipMapping,
            currentUserId = uiState.currentUser.id,
            toggleFriendClick = { onFriendClick(it) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        ExpensesSection()
    }
}
