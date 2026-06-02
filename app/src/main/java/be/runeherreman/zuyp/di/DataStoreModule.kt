package be.runeherreman.zuyp.di

import android.content.Context
import be.runeherreman.zuyp.data.local.datastore.SettingsDataStore
import be.runeherreman.zuyp.data.local.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore {
        return SettingsDataStore(context.dataStore)
    }
}