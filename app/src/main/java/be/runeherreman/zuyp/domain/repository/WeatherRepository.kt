package be.runeherreman.zuyp.domain.repository

import be.runeherreman.zuyp.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        hourly: String = "temperature_2m,rain",
        timezone: String = "auto",
        startDate: String,
        endDate: String
    ): Weather
}