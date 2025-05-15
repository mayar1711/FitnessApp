package com.example.fitnessapp.ui.home.view.composabled

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.R
import com.example.fitnessapp.model.datasource.model.VitalsData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HealthyData(vitalsData: VitalsData) {
    val context = LocalContext.current

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
                                DataCard(label = "Walk", value = vitalsData.steps.toString(), unit = "Steps", icon = R.drawable.baseline_favorite_24)
                                DataCard(label = "Calories", value = vitalsData.calories.toString(), unit = "kcal", icon = R.drawable.baseline_favorite_24)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                DataCard(label = "Heart Rate", value = "83", unit = "bpm", icon = R.drawable.baseline_favorite_24)
                                DataCard(label = "Sleep", value = vitalsData.sleep.toString(), unit = "hours", icon = R.drawable.baseline_favorite_24)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                DataCard(label = "Exercise", value = "50", unit = "mins", icon = R.drawable.baseline_favorite_24)
                                DataCard(label = "Distance", value = vitalsData.distance.toString(), unit = "m", icon = R.drawable.baseline_favorite_24)
                            }
                        }
                    }

                }
            }
        }
    }
}