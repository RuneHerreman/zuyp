package be.runeherreman.zuyp.di

import com.mapbox.search.autocomplete.PlaceAutocomplete
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {
    @Provides
    @Singleton
    fun providePlaceAutocomplete(): PlaceAutocomplete = PlaceAutocomplete.create()
}
