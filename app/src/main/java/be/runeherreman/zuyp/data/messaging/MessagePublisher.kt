package be.runeherreman.zuyp.data.messaging

interface MessagePublisher {
    suspend fun publishMessage(recipientId: String, message: String)
}
