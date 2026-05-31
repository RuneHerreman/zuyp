package be.runeherreman.zuyp.data.messaging

import android.util.Log
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LavinMQMessagePublisher @Inject constructor(
    private val hostName: String,
    private val queueName: String,
    private val connectionFactory: ConnectionFactory,
) : MessagePublisher {
    override suspend fun publishMessage(message: String) {
        withContext(Dispatchers.IO) {
            try {
                val connection = connectionFactory.newConnection(hostName)
                val channel = connection.createChannel()
                channel.queueDeclare(queueName, true, false, false, null)
                channel.basicPublish(
                    "",
                    queueName,
                    null,
                    message.toByteArray(Charsets.UTF_8)
                )
                Log.d("Messagebroker", "Published to Messagebroker")
                channel.close()
                connection.close()
            } catch (e: Exception) {
                Log.e("Messagebroker", "Error publishing to Messagebroker - ${e.message}")
            }
        }
    }
}