package be.runeherreman.zuyp.domain.useCases

import be.runeherreman.zuyp.data.messaging.MessagePublisher
import be.runeherreman.zuyp.domain.repository.UserRepository
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject

class SendZuypAlertUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val messagePublisher: MessagePublisher
) {
    suspend operator fun invoke(userId: UUID, hangoutId: String) {
        userRepository.getFriendsOfUser(userId).forEach { friend ->
            val message = JSONObject()
                .put("type", "zuyp_alert")
                .put("hangoutId", hangoutId)
                .toString()

            messagePublisher.publishMessage(friend.id.toString(), message)
        }
    }
}