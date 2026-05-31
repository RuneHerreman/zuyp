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
        generationtimeMs = generationtime_ms,
        utcOffsetSeconds = utc_offset_seconds,
        timezone = timezone,
        timezoneAbbreviation = timezone_abbreviation,
        elevation = elevation,
        hourlyUnits = hourly_units.toHourlyUnits(),
        hourly = hourly.toHourly()
    )
}

fun HourlyUnitsDTO.toHourlyUnits(): HourlyUnits {
    return HourlyUnits(
        time = time,
        temperature2m = temperature_2m,
        rain = rain
    )
}

fun HourlyDTO.toHourly(): Hourly {
    return Hourly(
        time = time,
        temperature2m = temperature_2m,
        rain = rain
    )
}
