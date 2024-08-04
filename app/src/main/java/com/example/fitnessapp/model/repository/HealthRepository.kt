package com.example.fitnessapp.model.repository

import android.content.Context
import com.example.fitnessapp.model.DataRecord
import java.time.ZonedDateTime

interface HealthRepository {
    fun checkForHealthConnectInstalled(context: Context): Int
    suspend fun checkPermissions(): Boolean

    suspend fun readStepsData(interval: Long): List<DataRecord>
    suspend fun writeStepsData(steps: Long, startTime: ZonedDateTime, endTime: ZonedDateTime)
    suspend fun readDistanceData(interval: Long): List<DataRecord>
    suspend fun writeDistanceData(distance: Double, startTime: ZonedDateTime, endTime: ZonedDateTime)
    suspend fun readCaloriesData(interval: Long): List<DataRecord>
    suspend fun writeCaloriesData(distance: Double, startTime: ZonedDateTime, endTime: ZonedDateTime)
    suspend fun readExerciseMinutesData(interval: Long): List<DataRecord>
    suspend fun writeExerciseMinutesData(distance: Double, startTime: ZonedDateTime, endTime: ZonedDateTime)
    suspend fun readSleepData(interval: Long): List<DataRecord>
    suspend fun writeSleepData(distance: Double, startTime: ZonedDateTime, endTime: ZonedDateTime)

}
