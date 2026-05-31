package be.runeherreman.zuyp.data.messaging

import com.rabbitmq.client.ConnectionFactory

interface MessageConsumer {
    fun startConsuming()
    fun stopConsuming()
    var onMessageReceived: ((String) -> Unit)
}