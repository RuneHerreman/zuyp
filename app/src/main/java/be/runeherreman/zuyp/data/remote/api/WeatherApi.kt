package be.runeherreman.zuyp.data.remote.api

import be.runeherreman.zuyp.data.remote.dto.WeatherDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,rain",
        @Query("timezone") timezone: String = "auto",
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): WeatherDTO
}