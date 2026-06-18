package be.runeherreman.zuyp.ui

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
class HomeScreenTest {
    @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1)
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    @get:Rule(order = 2) var composeTestRule = createAndroidComposeRule<MainActivity>()


    @Before
    fun setup() = hiltRule.inject()

    @Test
    fun homeScreen_isDisplayedOnLaunch() {
        composeTestRule
            .onNodeWithTag("home_screen")
            .assertIsDisplayed()
    }

    @Test
    fun createHangoutSheet_opens_whenButtonClicked() {
        composeTestRule
            .onNodeWithTag("create_hangout_btn")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Create a new hangout")
            .assertIsDisplayed()
    }
}