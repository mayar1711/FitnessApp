package com.example.fitnessapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.model.repository.HealthRepository
import com.example.fitnessapp.model.repository.HealthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HealthConnectViewModel (private val repository: HealthRepositoryImpl): ViewModel() {

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
            _mins.value = repository.exerciseMinutesData.readDataForInterval(interval)[0].metricValue
            _steps.value = repository.stepsData.readDataForInterval(interval)[0].metricValue
            _distance.value = repository.distanceData.readDataForInterval(interval)[0].metricValue
            _sleepDuration.value = repository.sleepData.readDataForInterval(interval).last().metricValue
            _calories.value = repository.caloriesData.readDataForInterval(interval)[0].metricValue
        }
    }
}
