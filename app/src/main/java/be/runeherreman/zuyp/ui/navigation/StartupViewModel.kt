package be.runeherreman.zuyp.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.runeherreman.zuyp.domain.useCases.users.GetStartupScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    getStartupScreen: GetStartupScreenUseCase
) : ViewModel() {

    /**
     * Route to open on launch. Falls back to [Screen.Home] when nothing has
     * been stored. Stays null until DataStore has emitted, so the NavHost can
     * wait for the real value before picking a start destination.
     */
    val startDestination: StateFlow<String?> =
        getStartupScreen()
            .map { route -> route ?: Screen.Home.route }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )
}
