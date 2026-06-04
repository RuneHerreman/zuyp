package be.runeherreman.zuyp.ui.navigation

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import be.runeherreman.zuyp.ui.discover.DiscoverScreen
import be.runeherreman.zuyp.ui.discover.DiscoverViewModel
import be.runeherreman.zuyp.ui.friends.FriendsScreen
import be.runeherreman.zuyp.ui.friends.FriendsViewModel
import be.runeherreman.zuyp.ui.hangout.HangoutViewModel
import be.runeherreman.zuyp.ui.home.HomeEvent
import be.runeherreman.zuyp.ui.home.HomeScreen
import be.runeherreman.zuyp.ui.home.HomeViewModel
import be.runeherreman.zuyp.ui.permissions.AppPermission
import be.runeherreman.zuyp.ui.permissions.MainViewModel
import be.runeherreman.zuyp.ui.permissions.PermissionManager
import be.runeherreman.zuyp.ui.profile.ProfileScreen
import be.runeherreman.zuyp.ui.profile.ProfileViewModel

@Composable
fun ZuypNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = viewModel(),
    discoverViewModel: DiscoverViewModel = viewModel(),
    friendsViewModel: FriendsViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    hangoutViewModel: HangoutViewModel = viewModel(),
    startupViewModel: StartupViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel()
) {
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val discoverUiState by discoverViewModel.uiState.collectAsStateWithLifecycle()
    val friendsUiState by friendsViewModel.uiState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val permissionRequest by mainViewModel.permissionRequest.collectAsStateWithLifecycle()

    val context = LocalContext.current

    // Start location
    val startDestination by startupViewModel.startDestination.collectAsStateWithLifecycle()
    val loadedRoute = startDestination ?: return

    // make sure selecting new preference doesn't navigate immediately
    val startRoute = rememberSaveable { loadedRoute }

    PermissionManager(
        permissionRequest = permissionRequest,
        onPermissionResult = { permission, granted ->
            mainViewModel.onPermissionResult(permission, granted)
        }
    )

    LaunchedEffect(loadedRoute) {
        mainViewModel.requestPermission(AppPermission.NOTIFICATION)
    }

    NavHost(
        navController = navController,
        startDestination = startRoute,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                uiState = homeUiState,
                onEvent = { event ->
                    when (event) {
                        is HomeEvent.HangoutClicked -> hangoutViewModel.selectHangout(event.hangoutId)
                        else -> homeViewModel.onEvent(event, context)
                    }
                }
            )
        }
        composable(Screen.Discover.route) {
            LaunchedEffect(Unit) {
                mainViewModel.requestPermission(AppPermission.LOCATION)
//                mainViewModel.requestPermission(AppPermission.BACKGROUND_LOCATION)
            }

            DiscoverScreen(
                uiState = discoverUiState,
                onLocationChanged = discoverViewModel::onUserLocationUpdates,
                onMarkerClick = discoverViewModel::openHangoutPopup,
                onMapClick = discoverViewModel::closeHangoutPopup
            )
        }
        composable(Screen.Friends.route) {
            FriendsScreen(
                uiState = friendsUiState,
                onCreateGroupOpen = friendsViewModel::openCreateGroup,
                onCreateGroup = friendsViewModel::createGroup,
                onEditGroupOpen = friendsViewModel::openEditGroup,
                onSaveGroupEdits = friendsViewModel::saveGroupEdits,
                onLeaveGroup = friendsViewModel::leaveGroup,
                onDeleteGroup = friendsViewModel::deleteGroup,
                onAddFriendOpen = friendsViewModel::openAddFriend,
                onAddFriend = friendsViewModel::addFriend,
                onRemoveFriend = friendsViewModel::removeFriend,
                onGroupClick = friendsViewModel::openGroupMembers,
                onFriendClick = friendsViewModel::openUserProfile,
                onDismissDialog = friendsViewModel::dismissDialog
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                uiState = profileUiState,
                onSettingsOpen = profileViewModel::openSettings,
                onSettingsClose = profileViewModel::closeSettings,
                onEditProfile = profileViewModel::onEditProfile,
                onEditProfileSave = profileViewModel::saveProfile,
                onEditProfileClose = profileViewModel::closeEditProfile,
                onStartupScreenSelect = profileViewModel::setStartupScreen,
                onHangoutClick = { hangoutViewModel.selectHangout(it.id.toString()) },
                onRefresh = profileViewModel::refresh,
                onFriendsClick = { navController.navigateToTab(Screen.Friends.route) },
                onGroupsClick = { navController.navigateToTab(Screen.Friends.route) },
                onEventsClick = { navController.navigateToTab(Screen.Home.route) }
            )
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
