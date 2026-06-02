package be.runeherreman.zuyp.domain.useCases.users

import be.runeherreman.zuyp.data.local.datastore.SettingsDataStore

class SetUserNotificationPreferenceUseCase(
    private val settingsDataStore: SettingsDataStore
) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsDataStore.setNotificationsEnabled(enabled)
    }
}