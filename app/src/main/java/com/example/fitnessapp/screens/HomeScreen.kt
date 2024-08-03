package com.example.fitnessapp.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import com.example.fitnessapp.R
import com.example.fitnessapp.utils.HealthConnectUtils
import kotlinx.coroutines.launch

@Composable
fun HealthConnectScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val interval: Long = 7

    var steps by remember { mutableStateOf("0") }
    var mins by remember { mutableStateOf("0") }
    var distance by remember { mutableStateOf("0") }
    var sleepDuration by remember { mutableStateOf("00:00") }

    var showHealthConnectInstallPopup by remember { mutableStateOf(false) }

    val requestPermissions = rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
        if (granted.containsAll(HealthConnectUtils.PERMISSIONS)) {
            scope.launch {
                mins = HealthConnectUtils.exerciseMinutesData.readDataForInterval(interval)[0].metricValue
                steps = HealthConnectUtils.stepsData.readDataForInterval(interval)[0].metricValue
                distance = HealthConnectUtils.distanceData.readDataForInterval(interval)[0].metricValue
                sleepDuration = HealthConnectUtils.sleepData.readDataForInterval(interval).last().metricValue
            }
        } else {
            Toast.makeText(context, "Permissions are rejected", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(key1 = true) {
        when (HealthConnectUtils.checkForHealthConnectInstalled(context)) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                Toast.makeText(context, "Health Connect client is not available for this device", Toast.LENGTH_SHORT).show()
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                showHealthConnectInstallPopup = true
            }
            HealthConnectClient.SDK_AVAILABLE -> {
                if (HealthConnectUtils.checkPermissions()) {
                    mins = HealthConnectUtils.exerciseMinutesData.readDataForInterval(interval)[0].metricValue
                    steps = HealthConnectUtils.stepsData.readDataForInterval(interval)[0].metricValue
                    distance = HealthConnectUtils.distanceData.readDataForInterval(interval)[0].metricValue
                    sleepDuration = HealthConnectUtils.sleepData.readDataForInterval(interval).last().metricValue
                } else {
                    requestPermissions.launch(HealthConnectUtils.PERMISSIONS)
                }
            }
        }
    }

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
                text = "Saturday, 03 Aug",
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEBF5FF))
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Your daily goals almost done! 👏",
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

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
                                DataCard(label = "Calories", value = "0", unit = "kcal", icon = R.drawable.baseline_favorite_24)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                DataCard(label = "Heart Rate", value = "83", unit = "bpm", icon = R.drawable.baseline_favorite_24)
                                DataCard(label = "Sleep", value = sleepDuration, unit = "hours", icon = R.drawable.baseline_favorite_24)
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
}@Composable
fun DataCard(
    label: String,
    value: String,
    unit: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .size(150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = label,
                    color = Color.Black,
                    fontSize = 16.sp
                )
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.Blue,
                    modifier = Modifier.size(24.dp)
                )

            }

            Text(
                text = value,
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unit,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
