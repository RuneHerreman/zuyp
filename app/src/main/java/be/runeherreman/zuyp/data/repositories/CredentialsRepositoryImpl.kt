package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.data.local.secure.SecureStorage
import be.runeherreman.zuyp.domain.model.AmqpCredentials
import be.runeherreman.zuyp.domain.repository.CredentialsRepository
import javax.inject.Inject

class CredentialsRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage
) : CredentialsRepository {

    override fun getAmqpCredentials(): AmqpCredentials? {
        val username = secureStorage.getString(SecureStorage.KEY_AMQP_USERNAME) ?: return null
        val password = secureStorage.getString(SecureStorage.KEY_AMQP_PASSWORD) ?: return null
        val url      = secureStorage.getString(SecureStorage.KEY_AMQP_URL)      ?: return null
        val vhost    = secureStorage.getString(SecureStorage.KEY_AMQP_VHOST)    ?: return null
        val exchange = secureStorage.getString(SecureStorage.KEY_AMQP_EXCHANGE) ?: return null
        return AmqpCredentials(username, password, url, vhost, exchange)
    }

    override fun storeAmqpCredentials(credentials: AmqpCredentials) {
        secureStorage.putString(SecureStorage.KEY_AMQP_USERNAME, credentials.username)
        secureStorage.putString(SecureStorage.KEY_AMQP_PASSWORD, credentials.password)
        secureStorage.putString(SecureStorage.KEY_AMQP_URL,      credentials.url)
        secureStorage.putString(SecureStorage.KEY_AMQP_VHOST,    credentials.vhost)
        secureStorage.putString(SecureStorage.KEY_AMQP_EXCHANGE, credentials.exchange)
    }
}