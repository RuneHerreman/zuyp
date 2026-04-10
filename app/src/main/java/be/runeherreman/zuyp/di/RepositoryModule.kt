package be.runeherreman.zuyp.di

import be.runeherreman.zuyp.data.repositories.HangoutRepositoryImpl
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindHangoutRepository(impl: HangoutRepositoryImpl): HangoutRepository
}