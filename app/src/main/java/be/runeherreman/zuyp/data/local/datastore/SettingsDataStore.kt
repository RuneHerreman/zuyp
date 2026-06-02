package be.runeherreman.zuyp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

class SettingsDataStore(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val STARTUP_SCREEN = stringPreferencesKey("startup_screen")
    }

    /** Route of the screen to open on app start, or null when none is set. */
    val startupScreen: Flow<String?> =
        dataStore.data.map { prefs ->
            prefs[STARTUP_SCREEN]
        }

    suspend fun setStartupScreen(route: String) {
        dataStore.edit { prefs ->
            prefs[STARTUP_SCREEN] = route
        }
    }
}
