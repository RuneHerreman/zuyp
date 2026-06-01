package be.runeherreman.zuyp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import be.runeherreman.zuyp.ui.hangout.HangoutOverlay
import be.runeherreman.zuyp.ui.hangout.HangoutViewModel
import be.runeherreman.zuyp.ui.navigation.ZuypBottomBar
import be.runeherreman.zuyp.ui.navigation.ZuypNavGraph
import be.runeherreman.zuyp.ui.theme.ZuypTheme

@Composable
fun ZuypApp(
    hangoutViewModel: HangoutViewModel = viewModel(),
    initialHangoutId: String? = null,
) {
    LaunchedEffect(initialHangoutId) {
        if (initialHangoutId != null) {
            hangoutViewModel.selectHangout(initialHangoutId)
        }
    }

    ZuypTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val hangoutUiState by hangoutViewModel.uiState.collectAsStateWithLifecycle()

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
                    hangoutViewModel = hangoutViewModel
                )
            }

            HangoutOverlay(
                uiState = hangoutUiState,
                onDismiss = hangoutViewModel::dismissHangout,
                onFriendClick = hangoutViewModel::toggleFriendship,
                onUpdateAttendanceStatus = hangoutViewModel::toggleGoing
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
