package be.runeherreman.zuyp.di

import be.runeherreman.zuyp.data.repositories.CredentialsRepositoryImpl
import be.runeherreman.zuyp.domain.repository.CredentialsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SecureModule {

    @Binds
    @Singleton
    fun bindCredentialsRepository(impl: CredentialsRepositoryImpl): CredentialsRepository
}