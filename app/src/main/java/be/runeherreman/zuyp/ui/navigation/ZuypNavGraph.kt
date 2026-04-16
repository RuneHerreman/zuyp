package be.runeherreman.zuyp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import be.runeherreman.zuyp.ui.discover.DiscoverScreen
import be.runeherreman.zuyp.ui.discover.DiscoverViewModel
import be.runeherreman.zuyp.ui.friends.FriendsScreen
import be.runeherreman.zuyp.ui.friends.FriendsViewModel
import be.runeherreman.zuyp.ui.hangout.HangoutScreen
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
    val hangoutUiState by hangoutViewModel.uiState.collectAsStateWithLifecycle()


    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                uiState = homeUiState,
                onLocationClick = { hangout ->
                    homeViewModel.openMapsForHangout(hangout, context)
                },
                onHangoutClick = { homeViewModel.onHangoutClick(it, navController) }
            )
        }
        composable(Screen.Discover.route) {
            DiscoverScreen(
                uiState = discoverUiState
            )
        }
        composable(Screen.Friends.route) {
            FriendsScreen(
                uiState = friendsUiState
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                uiState = profileUiState
            )
        }

        composable(Screen.Hangout.route) { backStackEntry ->
            val hangoutId = backStackEntry.arguments?.getString("hangoutId")!!
            hangoutViewModel.loadHangout(hangoutId)
            HangoutScreen(
                uiState = hangoutUiState,
                onBackClick = { navController.popBackStack() },
                onFriendClick = hangoutViewModel::toggleFriendship
            )
        }
    }
}