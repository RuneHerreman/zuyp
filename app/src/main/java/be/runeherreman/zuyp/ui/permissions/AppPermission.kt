package be.runeherreman.zuyp.ui.permissions

import android.Manifest

enum class AppPermission {
    NOTIFICATION,
    FLASHLIGHT,
    LOCATION,
    CAMERA,
    BACKGROUND_LOCATION
}

fun AppPermission.toAndroidPermissions(): List<String> {
    return when (this) {
        AppPermission.NOTIFICATION -> listOf(Manifest.permission.POST_NOTIFICATIONS)
        AppPermission.FLASHLIGHT -> listOf(Manifest.permission.CAMERA)
        AppPermission.LOCATION -> listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        AppPermission.BACKGROUND_LOCATION -> listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        AppPermission.CAMERA -> listOf(Manifest.permission.CAMERA)
    }
}