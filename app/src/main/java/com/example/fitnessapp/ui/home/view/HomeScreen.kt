package com.example.fitnessapp.ui.home.view

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.fitnessapp.model.datasource.model.VitalsData
import com.example.fitnessapp.ui.home.view.composabled.HealthyData
import com.example.fitnessapp.ui.home.viewmodel.HealthConnectViewModel
import com.example.fitnessapp.ui.state.UiState
import com.example.fitnessapp.utils.requiredHealthPermission
import kotlinx.coroutines.launch


@Composable
fun HealthConnectScreen(viewModel: HealthConnectViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showSdkUnavailableDialog by remember { mutableStateOf(false) }
    var showInstallHealthConnectDialog by remember { mutableStateOf(false) }
    val vitalsUiState by viewModel.uiState.collectAsState()
    val healthConnectClient = remember {
        try {
            HealthConnectClient.getOrCreate(context)
        } catch (e: Exception) {
            Log.e("HealthConnectScreen", "Failed to get HealthConnectClient", e)
            null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract(),
        onResult = { grantedPermissions ->
            if (grantedPermissions.containsAll(requiredHealthPermission)) {
                Log.d("HealthConnectScreen", "All permissions granted after request.")
                viewModel.fetchHealthData()
            } else {
                Log.d("HealthConnectScreen", "Not all permissions granted after request.")
                Toast.makeText(context, "Some permissions were not granted. Features might be limited.", Toast.LENGTH_LONG).show()

            }
        }
    )

    fun checkAndRequestPermissions() {
        if (healthConnectClient == null) {
            Log.e("HealthConnectScreen", "HealthConnectClient is null, cannot proceed.")
            showSdkUnavailableDialog = true
            return
        }
        coroutineScope.launch {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            if (granted.containsAll(requiredHealthPermission)) {
                Log.d("HealthConnectScreen", "All permissions already granted.")
                viewModel.fetchHealthData()
            } else {
                Log.d("HealthConnectScreen", "Permissions not granted, launching request.")

                permissionLauncher.launch(requiredHealthPermission)
            }
        }
    }

    LaunchedEffect(key1 = healthConnectClient, key2 = lifecycleOwner) {
        if (healthConnectClient == null) {
            val status = HealthConnectClient.getSdkStatus(context)
            if (status == HealthConnectClient.SDK_UNAVAILABLE || status == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
                if (status == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
                    showInstallHealthConnectDialog = true
                } else {
                    showSdkUnavailableDialog = true
                }
                return@LaunchedEffect
            }
             Log.e("HealthConnectScreen", "HealthConnectClient null but SDK reported available.")

            return@LaunchedEffect
        }

        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            Log.d("HealthConnectScreen", "Screen Resumed, checking Health Connect status.")
            when (HealthConnectClient.getSdkStatus(context)) {
                HealthConnectClient.SDK_AVAILABLE -> {
                    Log.d("HealthConnectScreen", "SDK Available. Checking permissions.")
                    checkAndRequestPermissions()
                }
                HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                    Log.d("HealthConnectScreen", "SDK Provider update required.")
                    showInstallHealthConnectDialog = true
                }
                HealthConnectClient.SDK_UNAVAILABLE -> {
                    Log.d("HealthConnectScreen", "SDK Unavailable.")
                    showSdkUnavailableDialog = true
                }
                else -> {
                    Log.d("HealthConnectScreen", "SDK status unknown or other error.")
                    showSdkUnavailableDialog = true // Or a more generic error
                }
            }
        }
    }

    // Dialogs
    if (showSdkUnavailableDialog) {
        AlertDialog(
            onDismissRequest = { showSdkUnavailableDialog = false },
            title = { Text("SDK Unavailable") },
            text = { Text("Health Connect SDK is not available on this device.") },
            confirmButton = {
                Button(onClick = { showSdkUnavailableDialog = false }) { Text("OK") }
            }
        )
    }

    if (showInstallHealthConnectDialog) {
        AlertDialog(
            onDismissRequest = { showInstallHealthConnectDialog = false },
            title = { Text("Health Connect Required") },
            text = { Text("Please install or update Health Connect to use this feature.") },
            confirmButton = {
                Button(onClick = {
                    showInstallHealthConnectDialog = false

                    try {
                        val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
                            setPackage("com.android.vending")
                        }
                        context.startActivity(playStoreIntent)

                    } catch (e: Exception) {
                        Log.i("TAG", "HomeScreen: $e")
                        Toast.makeText(context, "Could not open Play Store.", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Install/Update") }
            },
            dismissButton = {
                Button(onClick = { showInstallHealthConnectDialog = false }) { Text("Cancel") }
            }
        )
    }

    when(vitalsUiState){
        is UiState.Success -> {
            HealthyData(vitalsData = (vitalsUiState as UiState.Success<VitalsData>).data)
        }
        is UiState.Loading -> {
            CircularProgressIndicator()
        }
        is UiState.Error -> {
            Text(text = (vitalsUiState as UiState.Error).message)
        }
        else -> {
            Text(text = "Something went wrong")
        }
    }

}
