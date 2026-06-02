package be.runeherreman.zuyp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LOCATION_ENABLED = booleanPreferencesKey("location_enabled")
    }

    val notificationsEnabled: Flow<Boolean> =
        dataStore.data.
        map { prefs ->
            prefs[NOTIFICATIONS_ENABLED] ?: false
        }

    val locationEnabled: Flow<Boolean> =
        dataStore.data.
        map { prefs ->
            prefs[LOCATION_ENABLED] ?: false
        }


    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setLocationEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[LOCATION_ENABLED] = enabled
        }
    }
}