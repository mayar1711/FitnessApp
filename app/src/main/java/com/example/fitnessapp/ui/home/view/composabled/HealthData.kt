package com.example.fitnessapp.ui.home.view.composabled

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import com.example.fitnessapp.R
import com.example.fitnessapp.model.repository.HealthRepositoryImpl
import com.example.fitnessapp.ui.home.viewmodel.HealthConnectViewModel
import com.example.fitnessapp.utils.PERMISSIONS
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HealthConnectScreen() {
   val viewModel = HealthConnectViewModel(HealthRepositoryImpl.getInstance())
    val context = LocalContext.current
    val steps by viewModel.steps.collectAsState()
    val mins by viewModel.mins.collectAsState()
    val distance by viewModel.distance.collectAsState()
    val sleepDuration by viewModel.sleepDuration.collectAsState()
     val calories by viewModel.calories.collectAsState()
    var showHealthConnectInstallPopup by remember { mutableStateOf(false) }
    val healthRepository = HealthRepositoryImpl.getInstance()

    val requestPermissions = rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
        if (granted.containsAll(PERMISSIONS)) {
            viewModel.fetchHealthData()
        } else {
            Toast.makeText(context, "Permissions are rejected", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(key1 = true) {
        when (healthRepository.checkForHealthConnectInstalled(context)) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                Toast.makeText(context, "Health Connect client is not available for this device", Toast.LENGTH_SHORT).show()
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                showHealthConnectInstallPopup = true
            }
            HealthConnectClient.SDK_AVAILABLE -> {
                if (healthRepository.checkPermissions()) {
                    viewModel.fetchHealthData()
                } else {
                    requestPermissions.launch(PERMISSIONS)
                }
            }
        }
    }
    val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMM"))

    Scaffold(topBar = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEF6FF), RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp))
                .padding(vertical = 16.dp, horizontal = 20.dp)
        ) {
            Text(
                text = "Hi,",
                fontSize = 28.sp,
                color = Color.Black
            )
            Text(
                text = currentDate,
                fontSize = 18.sp,
                color = Color.Gray
            )
        }
    }) {
        Surface(modifier = Modifier.fillMaxSize().padding(it)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Today's Summary",
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                DataCard(label = "Walk", value = steps, unit = "Steps", icon = R.drawable.baseline_favorite_24)
                                DataCard(label = "Calories", value = calories, unit = "kcal", icon = R.drawable.baseline_favorite_24)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                DataCard(label = "Heart Rate", value = "83", unit = "bpm", icon = R.drawable.baseline_favorite_24)
                                DataCard(label = "Sleep", value = sleepDuration, unit = "hours", icon = R.drawable.baseline_favorite_24)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                DataCard(label = "Exercise", value = mins, unit = "mins", icon = R.drawable.baseline_favorite_24)
                                DataCard(label = "Distance", value = distance, unit = "m", icon = R.drawable.baseline_favorite_24)
                            }
                        }
                    }

                    if (showHealthConnectInstallPopup) {
                        AlertDialog(
                            onDismissRequest = { showHealthConnectInstallPopup = false },
                            confirmButton = {
                                ClickableText(text = AnnotatedString("Install"), onClick = {
                                    showHealthConnectInstallPopup = false
                                    val uriString = "market://details?id=com.google.android.apps.healthdata&url=healthconnect%3A%2F%2Fonboarding"
                                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                                        setPackage("com.android.vending")
                                        data = Uri.parse(uriString)
                                    })
                                })
                            },
                            title = { Text(text = "Alert") },
                            text = { Text(text = "Health Connect is not installed") }
                        )
                    }
                }
            }
        }
    }
}
