package com.example.fitnessapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.model.repository.HealthRepository
import com.example.fitnessapp.model.repository.HealthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

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
            _mins.value = repository.readExerciseMinutesData(interval).last().metricValue
            _steps.value = repository.readStepsData(interval).last().metricValue
            _distance.value = repository.readDistanceData(interval).last().metricValue
            _sleepDuration.value = repository.readSleepData(interval).last().metricValue
            _calories.value = repository.readCaloriesData(interval).last().metricValue
        }
    }

    fun writeStepsData(steps: Long, startTime: ZonedDateTime, endTime: ZonedDateTime) {
        viewModelScope.launch {
            repository.writeStepsData(steps, startTime, endTime)
        }
    }

    fun writeDistanceData(distance: Double, startTime: ZonedDateTime, endTime: ZonedDateTime) {
        viewModelScope.launch {
            repository.writeDistanceData(distance, startTime, endTime)
        }
    }
    fun writeCaloriesData(distance: Double, startTime: ZonedDateTime, endTime: ZonedDateTime) {
        viewModelScope.launch {
            repository.writeCaloriesData(distance, startTime, endTime)
        }
    }
    fun writeExerciseMinutesData(distance: Double, startTime: ZonedDateTime, endTime: ZonedDateTime) {
        viewModelScope.launch {
            repository.writeExerciseMinutesData(distance, startTime, endTime)
        }
    }
    fun writeSleepData(distance: Double, startTime: ZonedDateTime, endTime: ZonedDateTime) {
        viewModelScope.launch {
            repository.writeSleepData(distance, startTime, endTime)
        }
    }
}
