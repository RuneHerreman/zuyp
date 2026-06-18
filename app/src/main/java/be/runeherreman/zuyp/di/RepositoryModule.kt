package be.runeherreman.zuyp.di

import be.runeherreman.zuyp.data.geofence.GeofenceSyncSchedulerImpl
import be.runeherreman.zuyp.data.repositories.AddressRepositoryMapboxImpl
import be.runeherreman.zuyp.data.repositories.GeofenceRepositoryMapboxImpl
import be.runeherreman.zuyp.data.repositories.WeatherRepositoryImpl
import be.runeherreman.zuyp.data.repositories.room.ExpenseRepositoryRoomImpl
import be.runeherreman.zuyp.data.repositories.room.GroupRepositoryRoomImpl
import be.runeherreman.zuyp.data.repositories.room.HangoutRepositoryRoomImpl
import be.runeherreman.zuyp.data.repositories.room.UserRepositoryRoomImpl
import be.runeherreman.zuyp.data.repositories.sensors.ShakeRepositoryImpl
import be.runeherreman.zuyp.data.workers.geofencing.HydrationReminderScheduler
import be.runeherreman.zuyp.domain.geofence.GeofenceSyncScheduler
import be.runeherreman.zuyp.domain.geofence.HydrationScheduler
import be.runeherreman.zuyp.domain.repository.AddressRepository
import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import be.runeherreman.zuyp.domain.repository.GeoFenceRepository
import be.runeherreman.zuyp.domain.repository.GroupRepository
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import be.runeherreman.zuyp.domain.repository.UserRepository
import be.runeherreman.zuyp.domain.repository.WeatherRepository
import be.runeherreman.zuyp.domain.repository.sensors.ShakeRepository
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

    @Binds
    @Singleton
    fun bindShakeRepository(impl: ShakeRepositoryImpl): ShakeRepository

    @Binds
    @Singleton
    fun bindGeofenceRepository(impl: GeofenceRepositoryMapboxImpl): GeoFenceRepository

    @Binds
    @Singleton
    fun bindHydrationScheduler(impl: HydrationReminderScheduler): HydrationScheduler

    @Binds
    @Singleton
    fun bindGeofenceSyncScheduler(impl: GeofenceSyncSchedulerImpl): GeofenceSyncScheduler
}
