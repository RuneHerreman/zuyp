package be.runeherreman.zuyp.data.remote.dto

import be.runeherreman.zuyp.domain.model.Hourly
import be.runeherreman.zuyp.domain.model.HourlyUnits
import be.runeherreman.zuyp.domain.model.Weather

data class WeatherDTO(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val hourly_units: HourlyUnitsDTO,
    val hourly: HourlyDTO
)

data class HourlyUnitsDTO(
    val time: String,
    val temperature_2m: String,
    val rain: String
)

data class HourlyDTO(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val rain: List<Double>
)


fun WeatherDTO.toWeather(): Weather {
    return Weather(
        latitude = latitude,
        longitude = longitude,
        generationtime_ms = generationtime_ms,
        utc_offset_seconds = utc_offset_seconds,
        timezone = timezone,
        timezone_abbreviation = timezone_abbreviation,
        elevation = elevation,
        hourly_units = hourly_units.toHourlyUnits(),
        hourly = hourly.toHourly()
    )
}

fun HourlyUnitsDTO.toHourlyUnits(): HourlyUnits {
    return HourlyUnits(
        time = time,
        temperature_2m = temperature_2m,
        rain = rain
    )
}

fun HourlyDTO.toHourly(): Hourly {
    return Hourly(
        time = time,
        temperature_2m = temperature_2m,
        rain = rain
    )
}