package be.runeherreman.zuyp.di

import be.runeherreman.zuyp.BuildConfig
import be.runeherreman.zuyp.data.remote.api.WeatherApi
import be.runeherreman.zuyp.domain.model.Weather
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }.value

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val newRequest = request.newBuilder().build()
                chain.proceed(newRequest)
            }
            .build()
    }.value

    @Provides
    @Singleton
    fun provideRetrofit(okHttp: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.WEATHER_API_BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)
}