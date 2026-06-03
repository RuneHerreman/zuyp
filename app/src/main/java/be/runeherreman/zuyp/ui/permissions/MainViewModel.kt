package be.runeherreman.zuyp.ui.permissions

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel: ViewModel() {
    private val _permissionRequest = MutableStateFlow<AppPermission?>(null)

    val permissionRequest: StateFlow<AppPermission?> = _permissionRequest

    fun requestPermission(permission: AppPermission) {
        _permissionRequest.value = permission
    }

    fun onPermissionResult(permission: AppPermission, granted: Boolean) {
        Log.d("Permission", "$permission granted: $granted")
        _permissionRequest.value = null
    }
}