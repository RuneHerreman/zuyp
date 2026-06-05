package be.runeherreman.zuyp.di

import be.runeherreman.zuyp.BuildConfig
import be.runeherreman.zuyp.data.messaging.LavinMQMessageConsumer
import be.runeherreman.zuyp.data.messaging.LavinMQMessagePublisher
import be.runeherreman.zuyp.data.messaging.MessageConsumer
import be.runeherreman.zuyp.data.messaging.MessagePublisher
import be.runeherreman.zuyp.domain.model.AmqpCredentials
import be.runeherreman.zuyp.domain.usecases.credentials.GetAmqpCredentialsUseCase
import be.runeherreman.zuyp.domain.usecases.credentials.StoreAmqpCredentialsUseCase
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
    fun provideConnectionFactory(
        getCredentials: GetAmqpCredentialsUseCase,
        storeCredentials: StoreAmqpCredentialsUseCase,
    ): ConnectionFactory {
        val credentials = getCredentials() ?: AmqpCredentials(
            username = BuildConfig.AMQP_USERNAME,
            password = BuildConfig.AMQP_PASSWORD,
            url = BuildConfig.AMQP_URL,
            vhost = BuildConfig.AMQP_VHOST,
            exchange = BuildConfig.AMQP_EXCHANGE,
        ).also { storeCredentials(it) }

        val hostParts = credentials.url.split(":", limit = 2)
        val host = hostParts.firstOrNull().orEmpty()
        val port = hostParts.getOrNull(1)?.toIntOrNull() ?: ConnectionFactory.DEFAULT_AMQP_PORT

        return ConnectionFactory().apply {
            username    = credentials.username
            password    = credentials.password
            this.host   = host
            this.port   = port
            virtualHost = credentials.vhost
            useSslProtocol()
        }
    }

    @Provides
    @Singleton
    fun provideMessagePublisher(factory: ConnectionFactory, getCredentials: GetAmqpCredentialsUseCase): MessagePublisher =
        LavinMQMessagePublisher(getCredentials()?.exchange ?: BuildConfig.AMQP_EXCHANGE, factory)

    @Provides
    @Singleton
    fun provideMessageConsumer(factory: ConnectionFactory, getCredentials: GetAmqpCredentialsUseCase): MessageConsumer =
        LavinMQMessageConsumer(getCredentials()?.exchange ?: BuildConfig.AMQP_EXCHANGE, factory)
}
