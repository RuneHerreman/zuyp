package be.runeherreman.zuyp.domain.usecases.api

import be.runeherreman.zuyp.domain.model.Weather
import be.runeherreman.zuyp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
){
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        hourly: String,
        timezone: String,
        startDate: String,
        endDate: String
    ): Weather {
        return weatherRepository.getWeather(
            latitude = latitude,
            longitude = longitude,
            hourly = hourly,
            timezone = timezone,
            startDate = startDate,
            endDate = endDate
        )
    }
}