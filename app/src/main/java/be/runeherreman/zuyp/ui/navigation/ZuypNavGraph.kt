package be.runeherreman.zuyp.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun ZuypNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
    // Add view models here
    // ex.     homeViewModel: HomeViewModel = viewModel(),
) {
    // Add UiState here
    // ex.     val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Add composables here
        // ----- EXAMPLE -----
//        composable(Screen.Home.route) {
//            HomeScreen(
//                uiState = homeUiState,
//                onEmailChanged = {
//                    homeViewModel.onEmailChanged(it)
//                },
//                onOrderDessertClick = { navController.navigate(Screen.Desserts.route) },
//                onSubscribeClick = {
//                    homeViewModel.onSubscribeClicked()
//                }
//            )
//        }

        composable(Screen.Home.route) {
            Text(text = "Home Screen")
        }
        composable(Screen.Discover.route) {
            Text(text = "Discover Screen")
        }
        composable(Screen.Friends.route) {
            Text(text = "Friends Screen")
        }
        composable(Screen.Profile.route) {
            Text(text = "Profile Screen")
        }
    }
}