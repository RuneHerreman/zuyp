package be.runeherreman.zuyp.ui.permissions

import android.Manifest

enum class AppPermission {
    NOTIFICATION,
    FLASHLIGHT,
}

fun AppPermission.toAndroidPermission(): String {
    return when (this) {
        AppPermission.NOTIFICATION -> Manifest.permission.POST_NOTIFICATIONS
        AppPermission.FLASHLIGHT -> Manifest.permission.CAMERA
    }
}
