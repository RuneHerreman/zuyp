package be.runeherreman.zuyp.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun PermissionManager(
    permissionRequest: AppPermission?,
    onPermissionResult: (AppPermission, Boolean) -> Unit
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.values.all { it }
        permissionRequest?.let {
            onPermissionResult(it, allGranted)
        }
    }

    LaunchedEffect(permissionRequest) {
        permissionRequest ?: return@LaunchedEffect
        val permission = permissionRequest.toAndroidPermissions().toTypedArray()
        permissionLauncher.launch(permission)
    }
}