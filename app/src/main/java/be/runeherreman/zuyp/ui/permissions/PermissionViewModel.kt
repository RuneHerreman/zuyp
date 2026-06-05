package be.runeherreman.zuyp.ui.permissions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Outcome of a single permission request, delivered as a one-shot event. */
data class PermissionResult(val permission: AppPermission, val granted: Boolean)

/**
 * Single source of truth for runtime permission requests.
 *
 * Callers express intent via [requestPermission]; the [PermissionManager] mounted in the
 * nav graph observes [permissionRequest], launches the system dialog and reports back through
 * [onPermissionResult]. Callers that need to act on a grant collect [permissionResults] rather
 * than touching the Android permission APIs themselves.
 */
class PermissionViewModel : ViewModel() {
    private val _permissionRequest = MutableStateFlow<AppPermission?>(null)
    val permissionRequest: StateFlow<AppPermission?> = _permissionRequest

    // One-shot events: a grant happens once, it is not state that persists.
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
