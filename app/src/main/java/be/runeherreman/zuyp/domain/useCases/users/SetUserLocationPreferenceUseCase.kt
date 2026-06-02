package be.runeherreman.zuyp.domain.useCases.users

import be.runeherreman.zuyp.data.local.datastore.SettingsDataStore
import javax.inject.Inject

class SetUserLocationPreferenceUseCase @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsDataStore.setLocationEnabled(enabled)
    }
}