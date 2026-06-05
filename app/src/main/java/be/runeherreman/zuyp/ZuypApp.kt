package be.runeherreman.zuyp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
import be.runeherreman.zuyp.ui.discover.DiscoverViewModel
import be.runeherreman.zuyp.ui.discover.components.HangoutPopup
import be.runeherreman.zuyp.ui.hangout.HangoutEvent
import be.runeherreman.zuyp.ui.hangout.HangoutOverlay
import be.runeherreman.zuyp.ui.hangout.HangoutViewModel
import be.runeherreman.zuyp.ui.navigation.ZuypBottomBar
import be.runeherreman.zuyp.ui.navigation.ZuypNavGraph
import be.runeherreman.zuyp.ui.permissions.AppPermission
import be.runeherreman.zuyp.ui.permissions.PermissionViewModel
import be.runeherreman.zuyp.ui.theme.ZuypTheme
import be.runeherreman.zuyp.ui.utils.openMapsForHangout

@Composable
fun ZuypApp(
    hangoutViewModel: HangoutViewModel = viewModel(),
    discoverViewModel: DiscoverViewModel = viewModel(),
    permissionViewModel: PermissionViewModel = viewModel(),
    initialHangoutId: String? = null,
    onHangoutConsumed: () -> Unit = {},
) {
    LaunchedEffect(initialHangoutId) {
        if (initialHangoutId != null) {
            hangoutViewModel.selectHangout(initialHangoutId)
            onHangoutConsumed()
        }
    }

    ZuypTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val hangoutUiState by hangoutViewModel.uiState.collectAsStateWithLifecycle()
        val discoverUiState by discoverViewModel.uiState.collectAsStateWithLifecycle()
        val context = LocalContext.current

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                bottomBar = {
                    ZuypBottomBar(
                        navController = navController,
                        currentDestination = currentDestination
                    )
                }
            ) { innerPadding ->
                ZuypNavGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    hangoutViewModel = hangoutViewModel,
                    discoverViewModel = discoverViewModel,
                    permissionViewModel = permissionViewModel
                )
            }

            // Hangout popup -> covers the bottom bar
            val selectedHangout = discoverUiState.selectedHangout
            AnimatedVisibility(
                visible = discoverUiState.hangoutPopupOpen && selectedHangout != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                if (selectedHangout != null) {
                    val currentUserStatus = selectedHangout.attendees
                        .firstOrNull { it.id == discoverUiState.currentUserId }
                        ?.attendanceStatus

                    HangoutPopup(
                        hangout = selectedHangout,
                        currentUserStatus = currentUserStatus,
                        onClose = discoverViewModel::closeHangoutPopup,
                        onOpenDetails = {
                            hangoutViewModel.selectHangout(selectedHangout.id.toString())
                            discoverViewModel.closeHangoutPopup()
                        },
                        onToggleGoing = { discoverViewModel.toggleAttendance(AttendanceStatus.GOING) },
                        onToggleNotInterested = { discoverViewModel.toggleAttendance(AttendanceStatus.NOT_INTERESTED) },
                        onLocationClick = { openMapsForHangout(it, context) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Clicking hangout opens this screen
            HangoutOverlay(
                uiState = hangoutUiState,
                permissionViewModel = permissionViewModel,
                onEvent = { event ->
                    when (event) {
                        HangoutEvent.CameraClicked -> permissionViewModel.requestPermission(AppPermission.CAMERA)
                        HangoutEvent.ShareExternal -> hangoutViewModel.shareHangoutExternally(hangoutUiState.hangout, context)
                        else -> hangoutViewModel.onEvent(event)
                    }
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_7
)
@Composable
fun ZuypAppPreview() {
    ZuypApp()
}
