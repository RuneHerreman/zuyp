package be.runeherreman.zuyp.domain.model

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun generateWeatherPrediction(weather: Weather, hangout: Hangout): String {
    if (weather.hourly.temperature2m.isEmpty()) return "Weather data unavailable"

    val now = LocalDateTime.now()
    val hourIndex = if (hangout.startDate.isBefore(now)) {
        weather.hourly.temperature2m.size - 1
    } else {
        val hoursDiff = ChronoUnit.HOURS.between(now, hangout.startDate).toInt()
        minOf(hoursDiff, weather.hourly.temperature2m.size - 1).coerceAtLeast(0)
    }

    val temperature = weather.hourly.temperature2m.getOrNull(hourIndex)?.toInt() ?: 0
    val rain = weather.hourly.rain.getOrNull(hourIndex) ?: 0.0

    val weatherStatus = when {
        rain > 5.0 -> "Heavy rain"
        rain > 1.0 -> "Light rain"
        else -> "Clear skies"
    }

    val styleTip = when {
        rain > 5.0 -> "Wear rain coat"
        rain > 1.0 && temperature < 15 -> "Wear sweater, rain protection"
        rain > 1.0 && temperature >= 20 -> "T-shirt w/ light rain jacket"
        rain > 1.0 -> "Bring umbrella / light rain jacket"
        temperature < 7 -> "Dress warmly w/ winter jacket"
        temperature < 15 -> "Wear a sweater"
        temperature < 22 -> "Wear a light jacket"
        else -> "T-shirt and shorts"
    }

    return "$temperature°C • $weatherStatus • $styleTip"
}
