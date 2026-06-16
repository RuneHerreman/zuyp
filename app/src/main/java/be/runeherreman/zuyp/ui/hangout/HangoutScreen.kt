package be.runeherreman.zuyp.ui.hangout

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import be.runeherreman.zuyp.ui.hangout.utils.copyImageIntoAppStorage
import be.runeherreman.zuyp.ui.hangout.utils.expenseImageUri
import be.runeherreman.zuyp.ui.hangout.utils.newExpenseImageFile
import be.runeherreman.zuyp.ui.permissions.AppPermission
import be.runeherreman.zuyp.ui.permissions.PermissionViewModel
import java.io.File
import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.ui.hangout.components.AddExpenseDialog
import be.runeherreman.zuyp.ui.hangout.components.AttendeesSection
import be.runeherreman.zuyp.ui.hangout.components.BackButton
import be.runeherreman.zuyp.ui.hangout.components.DeleteButton
import be.runeherreman.zuyp.ui.hangout.components.HangoutActionButtons
import be.runeherreman.zuyp.ui.hangout.components.HangoutHeader
import be.runeherreman.zuyp.ui.hangout.components.PrivateBadge
import be.runeherreman.zuyp.ui.utils.openMapsForHangout
import be.runeherreman.zuyp.ui.hangout.components.ShareHangoutPopup
import be.runeherreman.zuyp.ui.friends.components.UserProfileDialog
import be.runeherreman.zuyp.ui.hangout.components.expenses.ExpenseDetailDialog
import be.runeherreman.zuyp.ui.hangout.components.expenses.ExpensesSection

@Composable
fun HangoutOverlay(
    uiState: HangoutUiState,
    permissionViewModel: PermissionViewModel,
    onEvent: (HangoutEvent) -> Unit = {},
) {
    val context = LocalContext.current
    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) pendingPhotoFile?.absolutePath?.let { onEvent(HangoutEvent.ExpenseImageCaptured(it)) }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onEvent(HangoutEvent.ExpenseImageCaptured(copyImageIntoAppStorage(context, it))) }
    }

    LaunchedEffect(Unit) {
        permissionViewModel.permissionResults.collect { (permission, granted) ->
            if (permission == AppPermission.CAMERA && granted) {
                val file = newExpenseImageFile(context)
                pendingPhotoFile = file
                takePictureLauncher.launch(expenseImageUri(context, file))
            }
        }
    }

    val handleEvent: (HangoutEvent) -> Unit = { event ->
        when (event) {
            HangoutEvent.GalleryClicked -> pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
            else -> onEvent(event)
        }
    }

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
                onEvent = handleEvent
            )
        }
    }

    if (uiState.isShareSheetOpen) {
        ShareHangoutPopup(
            users = uiState.allUsers,
            selectedIds = uiState.selectedInviteeIds,
            isSending = uiState.isSendingInvites,
            onToggle = { handleEvent(HangoutEvent.ToggleInvitee(it)) },
            onInvite = { handleEvent(HangoutEvent.SendInvites) },
            onClearSelection = { handleEvent(HangoutEvent.ClearInvitees) },
            onShareExternal = { handleEvent(HangoutEvent.ShareExternal) },
            onDismiss = { handleEvent(HangoutEvent.CloseShare) }
        )
    }
}

@Composable
fun HangoutScreen(
    uiState: HangoutUiState,
    onEvent: (HangoutEvent) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    BackHandler(onBack = { onEvent( HangoutEvent.BackClicked) })

    if (uiState.isError) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hangout not found", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    // During the AnimatedVisibility exit animation the screen keeps composing
    // for a frame after the hangout has been cleared, so bail out if it's gone.
    val hangout = uiState.hangout ?: return

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
            if (hangout.creator.id == uiState.currentUser.id) {
                DeleteButton(onDeleteClick = { onEvent(HangoutEvent.DeleteHangout(hangout.id)) })
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        if (hangout.private) {
            PrivateBadge()
        }

        HangoutHeader(
            hangout = hangout,
            uiState = uiState,
            onLocationClick = { openMapsForHangout(it, context) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        HangoutActionButtons(
            attendanceStatus = uiState.currentUserAttendanceStatus,
            toggleGoingClick = {
                onEvent(HangoutEvent.UpdateAttendance(hangout, AttendanceStatus.GOING))
            },
            toggleNotInterestedClick = {
                onEvent(HangoutEvent.UpdateAttendance(hangout, AttendanceStatus.NOT_INTERESTED))
            },
            onShareClick = { onEvent(HangoutEvent.ShareClicked) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        val goingAttendees = hangout.attendees.filter {
            it.attendanceStatus == AttendanceStatus.GOING || it.attendanceStatus == AttendanceStatus.PRESENT
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
