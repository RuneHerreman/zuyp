package be.runeherreman.zuyp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import be.runeherreman.zuyp.ui.home.HomeScreen
import be.runeherreman.zuyp.ui.home.HomeViewModel
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
    hangoutViewModel: HangoutViewModel = viewModel()
) {
    val context = LocalContext.current
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val discoverUiState by discoverViewModel.uiState.collectAsStateWithLifecycle()
    val friendsUiState by friendsViewModel.uiState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                uiState = homeUiState,
                onLocationClick = { homeViewModel.openMapsForHangout(it, context) },
                onHangoutClick = { hangoutViewModel.selectHangout(it.id.toString()) },
                onSearchOpen = homeViewModel::openSearch,
                onSearchClose = homeViewModel::closeSearch,
                onSearchQueryChange = homeViewModel::onSearchQueryChange,
                onRefresh = homeViewModel::refresh,
                onZuypAlertClick = homeViewModel::sendZuypAlert,
                onCreateHangoutOpen = homeViewModel::openCreateHangout,
                onCreateHangoutClose = homeViewModel::closeCreateHangout,
                onAddressQueryChange = homeViewModel::onAddressQueryChange,
                onAddressSelect = homeViewModel::selectAddress,
                onAddressClear = homeViewModel::clearAddress,
                onCreateHangout = homeViewModel::createHangout
            )
        }
        composable(Screen.Discover.route) {
            DiscoverScreen(
                uiState = discoverUiState,
                onLocationChanged = discoverViewModel::onUserLocationUpdates,
                onMarkerClick = discoverViewModel::openHangoutPopup,
            )
        }
        composable(Screen.Friends.route) {
            FriendsScreen(uiState = friendsUiState)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                uiState = profileUiState,
                onSettingsOpen = profileViewModel::openSettings,
                onSettingsClose = profileViewModel::closeSettings,
                onEditProfile = profileViewModel::onEditProfile,
                onNotificationsToggle = profileViewModel::setNotificationsEnabled,
                onLocationSharingToggle = profileViewModel::setLocationSharingEnabled,
                onHangoutClick = { hangoutViewModel.selectHangout(it.id.toString()) }
            )
        }
    }
}
