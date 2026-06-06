package be.runeherreman.zuyp.data.messaging

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PublishMessageTest {
    @Test
    fun publishMessage_sendsToQueue() = runTest {
        val factory = mockk<ConnectionFactory>()
        val connection = mockk<Connection>(relaxed = true)
        val channel = mockk<Channel>(relaxed = true)

        every { factory.newConnection() } returns connection
        every { connection.createChannel() } returns channel

        val publisher = LavinMQMessagePublisher("testExchange", factory)
        publisher.publishMessage("user-123", "Test message")

        verify {
            channel.basicPublish(
                "testExchange",
                "user-user-123",
                null,
                "Test message".toByteArray(Charsets.UTF_8)
            )
        }
    }
}