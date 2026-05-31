package be.runeherreman.zuyp.data.messaging

import org.json.JSONObject

sealed class NotificationMessage {
    data class HangoutInvite(val hangoutId: String) : NotificationMessage()
    data class ZuypAlert(val hangoutId: String) : NotificationMessage()

    // =========================================
    // Add different types of notifications here
    // =========================================
    companion object {
        fun fromJson(raw: String): NotificationMessage? = try {
            val json = JSONObject(raw)
            when (json.getString("type")) {
                "hangout_invite" -> HangoutInvite(json.getString("hangoutId"))
                "zuyp_alert" -> ZuypAlert(json.getString("hangoutId"))
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
