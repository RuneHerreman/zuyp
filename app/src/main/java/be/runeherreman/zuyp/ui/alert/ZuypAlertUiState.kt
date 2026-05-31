package be.runeherreman.zuyp.ui.alert

data class ZuypAlertUiState(
    val hangoutId: String = "",
    val title: String = "",
    val locationName: String = "",
    val startDate: String = "",
    val weather: String? = null,
)
