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
    /**
     * The engine reads the global Mapbox access token that the Maps SDK already
     * initialises from res/values/mapbox_access_token.xml — no extra wiring needed.
     */
    @Provides
    @Singleton
    fun providePlaceAutocomplete(): PlaceAutocomplete = PlaceAutocomplete.create()
}
