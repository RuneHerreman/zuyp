package be.runeherreman.zuyp.di

import be.runeherreman.zuyp.data.repositories.AddressRepositoryMapboxImpl
import be.runeherreman.zuyp.data.repositories.ExpenseRepositoryRoomImpl
import be.runeherreman.zuyp.data.repositories.GroupRepositoryRoomImpl
import be.runeherreman.zuyp.data.repositories.HangoutRepositoryRoomImpl
import be.runeherreman.zuyp.data.repositories.UserRepositoryRoomImpl
import be.runeherreman.zuyp.data.repositories.WeatherRepositoryImpl
import be.runeherreman.zuyp.domain.repository.AddressRepository
import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import be.runeherreman.zuyp.domain.repository.GroupRepository
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import be.runeherreman.zuyp.domain.repository.UserRepository
import be.runeherreman.zuyp.domain.repository.WeatherRepository
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
    fun bindHangoutRepository(impl: HangoutRepositoryRoomImpl): HangoutRepository

    @Binds
    @Singleton
    fun bindUserRepository(impl: UserRepositoryRoomImpl): UserRepository

    @Binds
    @Singleton
    fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    fun bindAddressRepository(impl: AddressRepositoryMapboxImpl): AddressRepository

    @Binds
    @Singleton
    fun bindGroupRepository(impl: GroupRepositoryRoomImpl): GroupRepository

    @Binds
    @Singleton
    fun bindExpenseRepository(impl: ExpenseRepositoryRoomImpl): ExpenseRepository
}