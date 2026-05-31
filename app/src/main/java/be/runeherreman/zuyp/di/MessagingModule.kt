package be.runeherreman.zuyp.di

import be.runeherreman.zuyp.BuildConfig
import be.runeherreman.zuyp.data.messaging.LavinMQMessageConsumer
import be.runeherreman.zuyp.data.messaging.LavinMQMessagePublisher
import be.runeherreman.zuyp.data.messaging.MessageConsumer
import be.runeherreman.zuyp.data.messaging.MessagePublisher
import com.rabbitmq.client.ConnectionFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MessagingModule {

    @Provides
    @Singleton
    fun provideConntectionFactory(): ConnectionFactory {
        val hostPort = BuildConfig.AMQP_URL
        val hostParts = hostPort.split(":", limit = 2)
        val host = hostParts.firstOrNull().orEmpty()
        val port = hostParts.getOrNull(1)?.toIntOrNull() ?: ConnectionFactory.DEFAULT_AMQP_PORT

        return ConnectionFactory().apply {
            username = BuildConfig.AMQP_USERNAME
            password = BuildConfig.AMQP_PASSWORD
            this.host = host
            this.port = port
        }
    }

    @Singleton
    @Provides
    fun provideInvitePublisher(factory: ConnectionFactory): MessagePublisher = lazy {
        LavinMQMessagePublisher(factory.host, BuildConfig.AMQP_SUBSCRIBE_TOPIC, factory)
    }.value

    @Singleton
    @Provides
    fun provideMessageConsumer(factory: ConnectionFactory): MessageConsumer = lazy {
        LavinMQMessageConsumer(factory.host, BuildConfig.AMQP_SUBSCRIBE_TOPIC, factory)
    }.value

}