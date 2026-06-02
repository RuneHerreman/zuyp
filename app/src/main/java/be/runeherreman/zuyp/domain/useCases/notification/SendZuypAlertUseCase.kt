package be.runeherreman.zuyp.domain.useCases.notification

import be.runeherreman.zuyp.data.messaging.MessagePublisher
import be.runeherreman.zuyp.domain.model.generateWeatherPrediction
import be.runeherreman.zuyp.domain.repository.HangoutRepository
import be.runeherreman.zuyp.domain.repository.UserRepository
import be.runeherreman.zuyp.domain.useCases.utils.GetWeatherForecastUseCase
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class SendZuypAlertUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val hangoutRepository: HangoutRepository,
    private val messagePublisher: MessagePublisher,
    private val getWeatherForecast: GetWeatherForecastUseCase,
) {
    suspend operator fun invoke(userId: UUID, hangoutId: UUID) {
        val hangout = hangoutRepository.getHangoutById(hangoutId) ?: return
        val formatter = DateTimeFormatter.ofPattern("MMM d yyyy, HH'h'mm")
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
            .put("type", "zuyp_alert")
            .put("hangoutId", hangoutId.toString())
            .put("title", hangout.title)
            .put("locationName", hangout.locationName)
            .put("startDate", hangout.startDate.format(formatter))
            .apply { weather?.let { put("weather", it) } }
            .toString()

        userRepository.getFriendsOfUser(userId).filter { it.id != userId }.forEach { friend ->
            messagePublisher.publishMessage(friend.id.toString(), message)
        }
    }
}
