package com.example.fitnessapp.utils

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HealthConnectPermissionsHandler(
    private val healthConnectClient: HealthConnectClient,
    private val coroutineScope: CoroutineScope,
    private val requiredPermissions: Set<String>,
    private val onPermissionsGranted: () -> Unit,
    private val onPermissionsDenied: () -> Unit,
    private val permissionRequester: (Set<String>) -> Unit
) {
    fun checkAndRequestPermissions() {
        coroutineScope.launch {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            if (granted.containsAll(requiredPermissions)) {
                Log.d("PermissionsHandler", "All permissions already granted.")
                onPermissionsGranted()
            } else {
                Log.d("PermissionsHandler", "Permissions not granted, requesting.")
                permissionRequester(requiredPermissions)
            }
        }
    }
}
