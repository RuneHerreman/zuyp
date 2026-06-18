package be.runeherreman.zuyp.domain.model

data class Weather(
    val latitude: Double,
    val longitude: Double,
    val generationtimeMs: Double,
    val utcOffsetSeconds: Int,
    val timezone: String,
    val timezoneAbbreviation: String,
    val elevation: Double,
    val hourlyUnits: HourlyUnits,
    val hourly: Hourly
)

data class HourlyUnits(
    val time: String,
    val temperature2m: String,
    val rain: String
)

data class Hourly(
    val time: List<String>,
    val temperature2m: List<Double>,
    val rain: List<Double>
)
