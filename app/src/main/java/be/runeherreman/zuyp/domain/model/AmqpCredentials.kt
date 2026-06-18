package be.runeherreman.zuyp.domain.model

data class AmqpCredentials(
    val username: String,
    val password: String,
    val url: String,
    val vhost: String,
    val exchange: String,
)
