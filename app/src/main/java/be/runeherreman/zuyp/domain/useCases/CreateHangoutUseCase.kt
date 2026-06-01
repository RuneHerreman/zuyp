package be.runeherreman.zuyp.domain.useCases

import be.runeherreman.zuyp.data.messaging.MessagePublisher
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.model.generateWeatherPrediction
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CreateHangoutUseCase @Inject constructor(
    private val hangoutRepository: HangoutRepository,
    private val messagePublisher: MessagePublisher,
    private val getWeatherForecast: GetWeatherForecastUseCase
) {
    suspend operator fun invoke(hangout: Hangout, invitees: List<User>) {
        hangoutRepository.createOrUpdateHangout(hangout)

        // NOTIFICATION
        val weather = try {
            val forecast = getWeatherForecast(
                latitude = hangout.latitude,
                longitude = hangout.longitude,
                hourly = "temperature_2m,rain",
                timezone = "auto",
                startDate = hangout.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                endDate = hangout.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            )
            generateWeatherPrediction(forecast, hangout)
        } catch (e: Exception) { null }

        val message = JSONObject()
            .put("type", "hangout_invite")
            .put("hangoutId", hangout.id)
            .put("title", hangout.title)
            .put("locationName", hangout.locationName)
            .put("startDate", hangout.startDate)
            .put("weather", weather)
            .toString()

        invitees.forEach { member ->
            messagePublisher.publishMessage(member.id.toString(), message)
        }
    }
}
