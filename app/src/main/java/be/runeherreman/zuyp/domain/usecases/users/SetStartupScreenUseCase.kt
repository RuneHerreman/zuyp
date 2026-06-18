package be.runeherreman.zuyp.domain.usecases.users

import be.runeherreman.zuyp.data.local.datastore.SettingsDataStore
import javax.inject.Inject

class SetStartupScreenUseCase @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    suspend operator fun invoke(route: String) {
        settingsDataStore.setStartupScreen(route)
    }
}
