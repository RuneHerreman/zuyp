package be.runeherreman.zuyp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
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
import be.runeherreman.zuyp.ui.hangout.utils.copyImageIntoAppStorage
import be.runeherreman.zuyp.ui.hangout.utils.expenseImageUri
import be.runeherreman.zuyp.ui.hangout.utils.newExpenseImageFile
import be.runeherreman.zuyp.ui.navigation.ZuypBottomBar
import be.runeherreman.zuyp.ui.navigation.ZuypNavGraph
import be.runeherreman.zuyp.ui.permissions.AppPermission
import be.runeherreman.zuyp.ui.permissions.MainViewModel
import be.runeherreman.zuyp.ui.theme.ZuypTheme
import java.io.File

@Composable
fun ZuypApp(
    hangoutViewModel: HangoutViewModel = viewModel(),
    discoverViewModel: DiscoverViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel(),
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
        val permissionRequest by mainViewModel.permissionRequest.collectAsStateWithLifecycle()
        val context = LocalContext.current

        // Tracks a pending camera launch that will fire once CAMERA permission is resolved.
        var pendingCameraLaunch by remember { mutableStateOf(false) }
        var pendingPhotoFile by remember { mutableStateOf<File?>(null) }

        val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok) pendingPhotoFile?.absolutePath?.let { hangoutViewModel.onExpenseImageCaptured(it) }
        }

        val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { hangoutViewModel.onExpenseImageCaptured(copyImageIntoAppStorage(context, it)) }
        }

        // When the CAMERA permission request is resolved (permissionRequest clears to null),
        // launch the camera if the user had clicked the camera button.
        LaunchedEffect(permissionRequest) {
            if (permissionRequest == null && pendingCameraLaunch) {
                pendingCameraLaunch = false
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    val file = newExpenseImageFile(context)
                    pendingPhotoFile = file
                    takePictureLauncher.launch(expenseImageUri(context, file))
                }
            }
        }

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
                    mainViewModel = mainViewModel
                )
            }

            // Discover marker popup — rendered over the Scaffold so it covers the bottom bar.
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            HangoutOverlay(
                uiState = hangoutUiState,
                onEvent = { event ->
                    when (event) {
                        HangoutEvent.CameraClicked -> { pendingCameraLaunch = true; mainViewModel.requestPermission(AppPermission.CAMERA) }
                        HangoutEvent.GalleryClicked -> pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
