package com.example.fitnessapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HealthConnectViewModel : ViewModel() {

    private val _steps = MutableStateFlow("0")
    val steps: StateFlow<String> = _steps

    private val _mins = MutableStateFlow("0")
    val mins: StateFlow<String> = _mins

    private val _distance = MutableStateFlow("0")
    val distance: StateFlow<String> = _distance

    private val _sleepDuration = MutableStateFlow("00:00")
    val sleepDuration: StateFlow<String> = _sleepDuration

    private val _calories = MutableStateFlow("00:00")
    val calories: StateFlow<String> = _calories

    private val interval: Long = 7

    fun fetchHealthData() {
        viewModelScope.launch {
            _mins.value = Repository.exerciseMinutesData.readDataForInterval(interval)[0].metricValue
            _steps.value = Repository.stepsData.readDataForInterval(interval)[0].metricValue
            _distance.value = Repository.distanceData.readDataForInterval(interval)[0].metricValue
            _sleepDuration.value = Repository.sleepData.readDataForInterval(interval).last().metricValue
            _calories.value = Repository.caloriesData.readDataForInterval(interval)[0].metricValue
        }
    }
}
