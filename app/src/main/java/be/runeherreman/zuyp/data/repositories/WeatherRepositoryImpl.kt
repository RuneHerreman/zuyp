package be.runeherreman.zuyp.data.repositories

import be.runeherreman.zuyp.data.remote.api.WeatherApi
import be.runeherreman.zuyp.data.remote.dto.HourlyDTO
import be.runeherreman.zuyp.data.remote.dto.HourlyUnitsDTO
import be.runeherreman.zuyp.data.remote.dto.WeatherDTO
import be.runeherreman.zuyp.data.remote.dto.toWeather
import be.runeherreman.zuyp.domain.model.Hourly
import be.runeherreman.zuyp.domain.model.HourlyUnits
import be.runeherreman.zuyp.domain.model.Weather
import be.runeherreman.zuyp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiClient: WeatherApi
): WeatherRepository {
    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        hourly: String,
        timezone: String,
        startDate: String,
        endDate: String
    ): Weather {
        return weatherApiClient.getForecast(
            latitude = latitude,
            longitude = longitude,
            hourly = hourly,
            timezone = timezone,
            startDate = startDate,
            endDate = endDate
        ).toWeather()
    }
}


