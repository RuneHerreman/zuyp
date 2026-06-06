package be.runeherreman.zuyp.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import be.runeherreman.zuyp.MainActivity
import be.runeherreman.zuyp.domain.usecases.hangouts.GetHangoutsUseCase
import be.runeherreman.zuyp.ui.home.HomeViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class HomeViewModelTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.activity.runOnUiThread {
            viewModel = ViewModelProvider(composeTestRule.activity)[HomeViewModel::class.java]
        }
    }

    @Test
    fun search_returnsMatchingHangouts() = runBlocking {
        viewModel.onSearchQueryChange("The")
        assert(viewModel.uiState.value.searchResults.all { it.title.contains("The", ignoreCase = true) })
    }
}