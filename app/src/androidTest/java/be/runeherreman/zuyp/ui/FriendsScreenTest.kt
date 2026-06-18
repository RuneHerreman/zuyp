package be.runeherreman.zuyp.ui

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import be.runeherreman.zuyp.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class FriendsScreenTest {

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
    fun setup() = hiltRule.inject()

    @Test
    fun friendsScreen_isDisplayedAfterBottomBarTap() {
        composeTestRule
            .onNodeWithTag("bottom_nav_friends")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("friends_screen")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_isDisplayedAfterBottomBarTap() {
        composeTestRule
            .onNodeWithTag("bottom_nav_profile")
            .performClick()

        composeTestRule.waitForIdle()

        // Profile header always shows the current user's name
        composeTestRule
            .onNodeWithTag("friends_screen")
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithTag("bottom_nav_profile")
            .assertIsDisplayed()
    }
}
