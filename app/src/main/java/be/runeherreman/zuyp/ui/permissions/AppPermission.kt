package be.runeherreman.zuyp.ui.permissions

import android.Manifest

enum class AppPermission {
    NOTIFICATION,
    FLASHLIGHT,
    LOCATION,
}

fun AppPermission.toAndroidPermissions(): String {
    return when (this) {
        AppPermission.NOTIFICATION -> Manifest.permission.POST_NOTIFICATIONS
        AppPermission.FLASHLIGHT -> Manifest.permission.CAMERA
        AppPermission.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION + Manifest.permission.ACCESS_COARSE_LOCATION
    }
}