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
    private val host: String,
    private val queueName: String,
    private val factory: ConnectionFactory
): MessageConsumer {
    private var consumerJob: Job? = null

    override fun startConsuming() {
        consumerJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = factory.newConnection(host)
                val channel = connection.createChannel()

                channel.queueDeclare(queueName, true, false, false, null)
                val deliverCallback = DeliverCallback { _, delivery ->
                    val message = String(delivery.body, Charsets.UTF_8)
                    onMessageReceived(message)
                }

                channel.basicConsume(queueName, true, deliverCallback, CancelCallback {
                    Log.w("Messagebroker", "Consumer callback")
                })

                while(isActive) delay(1000)
            } catch (e: Exception) {
                Log.e("Messagebroker", "Error publishing to Messagebroker - ${e.message}")
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