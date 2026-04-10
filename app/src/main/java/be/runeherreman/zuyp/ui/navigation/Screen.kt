package be.runeherreman.zuyp.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import be.runeherreman.zuyp.R

sealed class Screen(
    val route: String,
    @param:StringRes val labelResourceId: Int,
    val icon: ImageVector
) {
    data object Home: Screen("home", R.string.home_label, Icons.Filled.Home)
    data object Discover: Screen("home", R.string.home_label, Icons.Filled.LocationOn)
    data object Friends: Screen("home", R.string.home_label, Icons.Filled.Groups)
    data object Profile: Screen("home", R.string.home_label, Icons.Filled.Person)
}

val screens = listOf(
    Screen.Home,
    Screen.Discover,
    Screen.Friends,
    Screen.Profile
)