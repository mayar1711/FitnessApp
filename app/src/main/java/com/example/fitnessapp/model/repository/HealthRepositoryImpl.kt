package com.example.fitnessapp.model.repository

import android.content.Context
import android.widget.Toast
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import com.example.fitnessapp.model.datasource.CaloriesData
import com.example.fitnessapp.model.datasource.DistanceData
import com.example.fitnessapp.model.datasource.ExerciseMinutesData
import com.example.fitnessapp.model.datasource.SleepData
import com.example.fitnessapp.model.datasource.StepsData
import com.example.fitnessapp.utils.PERMISSIONS
import java.time.format.DateTimeFormatter

class HealthRepositoryImpl private constructor() : HealthRepository {

    companion object {
        private var INSTANCE: HealthRepositoryImpl? = null

        fun getInstance(): HealthRepositoryImpl {
            if (INSTANCE == null) {
                INSTANCE = HealthRepositoryImpl()
            }
            return INSTANCE!!
        }
    }
    var healthConnectClient: HealthConnectClient? = null

    val stepsData: StepsData by lazy { StepsData(healthConnectClient!!) }
    val distanceData: DistanceData by lazy { DistanceData(healthConnectClient!!) }
    val exerciseMinutesData: ExerciseMinutesData by lazy { ExerciseMinutesData(healthConnectClient!!) }
    val sleepData: SleepData by lazy { SleepData(healthConnectClient!!) }
    val caloriesData: CaloriesData by lazy { CaloriesData(healthConnectClient!!) }

    override fun checkForHealthConnectInstalled(context: Context): Int {
        val availabilityStatus =
            HealthConnectClient.getSdkStatus(context, "com.google.android.apps.healthdata")
        when (availabilityStatus) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                Toast.makeText(context, "Health Connect is not available on this device.", Toast.LENGTH_LONG).show()
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                Toast.makeText(context, "Please update your Health Connect provider.", Toast.LENGTH_LONG).show()

            }
            HealthConnectClient.SDK_AVAILABLE -> {
                healthConnectClient = HealthConnectClient.getOrCreate(context)
            }
        }
        return availabilityStatus
    }

    override suspend fun checkPermissions(): Boolean {
        val granted = healthConnectClient?.permissionController?.getGrantedPermissions()
        return granted?.containsAll(PERMISSIONS) ?: false
    }
}
