package be.runeherreman.zuyp.domain.useCases.users

import be.runeherreman.zuyp.data.local.datastore.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserNotificationPreferenceUseCase @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    operator fun invoke(): Flow<Boolean> = settingsDataStore.notificationsEnabled
}
