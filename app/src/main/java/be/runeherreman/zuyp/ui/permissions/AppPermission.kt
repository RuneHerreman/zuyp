package be.runeherreman.zuyp.ui.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

enum class AppPermission {
    NOTIFICATION,
    FLASHLIGHT,
    LOCATION,
    CAMERA,
    BACKGROUND_LOCATION
}

fun AppPermission.isGranted(context: Context): Boolean =
    toAndroidPermissions().all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
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