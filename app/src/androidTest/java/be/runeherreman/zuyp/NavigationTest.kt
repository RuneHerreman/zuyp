package be.runeherreman.zuyp

import android.Manifest
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun startDestination_isHome() {
        composeTestRule.onAllNodesWithTag("home_screen").fetchSemanticsNodes().isNotEmpty() // Home screen load
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
    }

    @Test
    fun bottomBar_navigatesToFriends() {
        composeTestRule.onAllNodesWithTag("home_screen").fetchSemanticsNodes().isNotEmpty() // Home screen load

        composeTestRule.onNodeWithTag("bottom_nav_friends").performClick() // Click friends tab
        composeTestRule.onAllNodesWithTag("friends_screen").fetchSemanticsNodes().isNotEmpty()
        composeTestRule.onNodeWithTag("friends_screen").assertIsDisplayed()
    }
}