package be.runeherreman.zuyp.data.messaging

import android.util.Log
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LavinMQMessageConsumer(
    private val exchange: String,
    private val factory: ConnectionFactory,
) : MessageConsumer {
    private var consumerJob: Job? = null

    override fun startConsuming(userId: String) {
        consumerJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = factory.newConnection()
                val channel = connection.createChannel()

                channel.exchangeDeclare(exchange, "direct", true)

                // Exclusive temporary queue — auto-deletes when the app disconnects
                val queue = channel.queueDeclare("", false, true, true, null)
                channel.queueBind(queue.queue, exchange, "user-$userId")

                val deliverCallback = DeliverCallback { _, delivery ->
                    val message = String(delivery.body, Charsets.UTF_8)
                    onMessageReceived(message)
                }

                channel.basicConsume(queue.queue, true, deliverCallback, CancelCallback {
                    Log.w("Messagebroker", "Consumer cancelled")
                })

                while (isActive) delay(1000)
            } catch (e: Exception) {
                Log.e("Messagebroker", "Consumer error - ${e.message}")
            }
        }
    }

    override fun stopConsuming() {
        consumerJob?.cancel()
    }

    override var onMessageReceived: (String) -> Unit = {
        Log.w("Messagebroker", "onMessageReceived not set")
    }
}
