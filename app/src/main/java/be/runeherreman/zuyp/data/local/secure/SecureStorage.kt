package be.runeherreman.zuyp.data.local.secure

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "zuyp_secure_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun putString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    companion object {
        const val KEY_AMQP_USERNAME = "amqp_username"
        const val KEY_AMQP_PASSWORD = "amqp_password"
        const val KEY_AMQP_URL      = "amqp_url"
        const val KEY_AMQP_VHOST    = "amqp_vhost"
        const val KEY_AMQP_EXCHANGE = "amqp_exchange"
    }
}