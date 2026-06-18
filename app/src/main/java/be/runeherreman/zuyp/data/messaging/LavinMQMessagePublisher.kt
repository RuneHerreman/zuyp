package be.runeherreman.zuyp.data.messaging

import android.util.Log
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LavinMQMessagePublisher @Inject constructor(
    private val exchange: String,
    private val factory: ConnectionFactory,
) : MessagePublisher {
    override suspend fun publishMessage(recipientId: String, message: String) {
        withContext(Dispatchers.IO) {
            try {
                val connection = factory.newConnection()
                val channel = connection.createChannel()
                channel.exchangeDeclare(exchange, "direct", true)
                channel.basicPublish(
                    exchange,
                    "user-$recipientId",
                    null,
                    message.toByteArray(Charsets.UTF_8)
                )
                Log.d("Messagebroker", "Published to user-$recipientId")
                channel.close()
                connection.close()
            } catch (e: Exception) {
                Log.e("Messagebroker", "Error publishing - ${e.message}")
            }
        }
    }
}
