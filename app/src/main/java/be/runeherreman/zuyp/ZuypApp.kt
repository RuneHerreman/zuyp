package be.runeherreman.zuyp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import be.runeherreman.zuyp.ui.navigation.ZuypBottomBar
import be.runeherreman.zuyp.ui.navigation.ZuypNavGraph
import be.runeherreman.zuyp.ui.theme.ZuypTheme

@Composable
fun ZuypApp() {
    ZuypTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

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
                modifier = Modifier.padding(innerPadding)
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