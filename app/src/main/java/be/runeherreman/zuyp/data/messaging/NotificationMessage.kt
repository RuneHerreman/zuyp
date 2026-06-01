package be.runeherreman.zuyp.data.messaging

import org.json.JSONObject

sealed class NotificationMessage {
    data class HangoutInvite(
        val hangoutId: String,
        val title: String,
        val locationName: String,
        val startDate: String,
        val weather: String? = null,
    ) : NotificationMessage()

    data class ZuypAlert(
        val hangoutId: String,
        val title: String,
        val locationName: String,
        val startDate: String,
        val weather: String? = null,
    ) : NotificationMessage()

    data class HangoutJoined(
        val username: String,
        val hangoutName: String,
        val location: String,
        val hangoutId: String
    ): NotificationMessage()

    // =========================================
    // Add different types of notifications here -> this comment is not AI generated, just looks better
    // =========================================
    companion object {
        fun fromJson(raw: String): NotificationMessage? = try {
            val json = JSONObject(raw)
            when (json.getString("type")) {
                "hangout_invite" -> HangoutInvite(
                    hangoutId = json.getString("hangoutId"),
                    title = json.getString("title"),
                    locationName = json.getString("locationName"),
                    startDate = json.getString("startDate"),
                    weather = json.optString("weather").ifBlank { null },
                )
                "zuyp_alert" -> ZuypAlert(
                    hangoutId = json.getString("hangoutId"),
                    title = json.getString("title"),
                    locationName = json.getString("locationName"),
                    startDate = json.getString("startDate"),
                    weather = json.optString("weather").ifBlank { null },
                )
                "hangout_joined" -> HangoutJoined(
                    hangoutId = json.getString("hangoutId"),
                    username = json.getString("username"),
                    hangoutName = json.getString("hangoutName"),
                    location = json.getString("location"),
                )
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
