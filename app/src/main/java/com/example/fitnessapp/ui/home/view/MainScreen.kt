package com.example.fitnessapp.ui.home.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.ui.home.viewmodel.HealthConnectViewModel

@Composable
fun HomeScreen() {
    val healthConnectViewModel: HealthConnectViewModel = viewModel()
    HealthConnectScreen(viewModel = healthConnectViewModel)
}
