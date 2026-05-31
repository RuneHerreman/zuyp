package be.runeherreman.zuyp.data.messaging

interface MessagePublisher {
    suspend fun publishMessage(message: String)
}