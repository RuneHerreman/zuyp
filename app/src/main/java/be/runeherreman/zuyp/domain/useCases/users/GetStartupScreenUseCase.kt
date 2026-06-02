package be.runeherreman.zuyp.domain.useCases.users

import be.runeherreman.zuyp.data.local.datastore.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStartupScreenUseCase @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    operator fun invoke(): Flow<String?> = settingsDataStore.startupScreen
}
