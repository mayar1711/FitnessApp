package com.example.fitnessapp.ui.home.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.fitnessapp.model.datasource.model.VitalsData
import com.example.fitnessapp.ui.home.view.composabled.*
import com.example.fitnessapp.ui.home.viewmodel.HealthConnectViewModel
import com.example.fitnessapp.ui.state.UiState
import com.example.fitnessapp.utils.HealthConnectClientProvider
import com.example.fitnessapp.utils.HealthConnectPermissionsHandler
import com.example.fitnessapp.utils.requiredHealthPermission
import com.example.fitnessapp.utils.SdkUnavailableDialog
import com.example.fitnessapp.utils.InstallHealthConnectDialog
import androidx.compose.ui.platform.testTag


@Composable
fun HealthConnectScreen(
    viewModel: HealthConnectViewModel = hiltViewModel(),
    showPermissionsFlow: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var showSdkUnavailableDialog by remember { mutableStateOf(false) }
    var showInstallHealthConnectDialog by remember { mutableStateOf(false) }

    val vitalsUiState by viewModel.uiState.collectAsState()

    val healthConnectClient = remember {
        HealthConnectClientProvider.getClient(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract(),
        onResult = { grantedPermissions ->
            if (grantedPermissions.containsAll(requiredHealthPermission)) {
                viewModel.fetchHealthData()
            } else {
                showSdkUnavailableDialog = true
            }
        }
    )

    val permissionsHandler = remember(healthConnectClient) {
        healthConnectClient?.let {
            HealthConnectPermissionsHandler(
                healthConnectClient = it,
                coroutineScope = coroutineScope,
                requiredPermissions = requiredHealthPermission,
                onPermissionsGranted = { viewModel.fetchHealthData() },
                onPermissionsDenied = { showSdkUnavailableDialog = true },
                permissionRequester = { permissions ->
                    permissionLauncher.launch(permissions)
                }
            )
        }
    }
    if (showPermissionsFlow) {
        LaunchedEffect(healthConnectClient, lifecycleOwner) {
            if (healthConnectClient == null) {
                when (HealthConnectClientProvider.getSdkStatus(context)) {
                    HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> showInstallHealthConnectDialog = true
                    HealthConnectClient.SDK_UNAVAILABLE -> showSdkUnavailableDialog = true
                    else -> showSdkUnavailableDialog = true
                }
                return@LaunchedEffect
            }

            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                when (HealthConnectClientProvider.getSdkStatus(context)) {
                    HealthConnectClient.SDK_AVAILABLE -> {
                        permissionsHandler?.checkAndRequestPermissions()
                    }
                    HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                        showInstallHealthConnectDialog = true
                    }
                    HealthConnectClient.SDK_UNAVAILABLE -> {
                        showSdkUnavailableDialog = true
                    }
                    else -> {
                        showSdkUnavailableDialog = true
                    }
                }
            }
        }
    }

    if (showSdkUnavailableDialog) {
        SdkUnavailableDialog { showSdkUnavailableDialog = false }
    }

    if (showInstallHealthConnectDialog) {
        InstallHealthConnectDialog { showInstallHealthConnectDialog = false }
    }

    when (vitalsUiState) {
        is UiState.Success ->{ HealthyData(vitalsData = (vitalsUiState as UiState.Success<VitalsData>).data)}
        is UiState.Loading -> CircularProgressIndicator(Modifier.testTag("LoadingIndicator"))
        is UiState.Error -> {
            val message = (vitalsUiState as UiState.Error).message
            Text(text = message)
        }
        else -> Text(text = "Something went wrong")
    }
}

