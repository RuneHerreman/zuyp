package be.runeherreman.zuyp.domain.repository

import be.runeherreman.zuyp.domain.model.AmqpCredentials

interface CredentialsRepository {
    fun getAmqpCredentials(): AmqpCredentials?
    fun storeAmqpCredentials(credentials: AmqpCredentials)
}