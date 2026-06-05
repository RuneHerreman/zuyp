package be.runeherreman.zuyp.ui.permissions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PermissionResult(val permission: AppPermission, val granted: Boolean)

class PermissionViewModel : ViewModel() {
    private val _permissionRequest = MutableStateFlow<AppPermission?>(null)
    val permissionRequest: StateFlow<AppPermission?> = _permissionRequest

    private val _permissionResults = MutableSharedFlow<PermissionResult>()
    val permissionResults: SharedFlow<PermissionResult> = _permissionResults

    fun requestPermission(permission: AppPermission) {
        _permissionRequest.value = permission
    }

    fun onPermissionResult(permission: AppPermission, granted: Boolean) {
        Log.d("Permission", "$permission granted: $granted")
        _permissionRequest.value = null
        viewModelScope.launch { _permissionResults.emit(PermissionResult(permission, granted)) }
    }
}
