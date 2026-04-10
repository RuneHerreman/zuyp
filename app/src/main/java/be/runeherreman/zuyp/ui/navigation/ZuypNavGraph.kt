package be.runeherreman.zuyp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost

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
    }
}