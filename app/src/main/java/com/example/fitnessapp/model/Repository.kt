package com.example.fitnessapp.model

import android.content.Context
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
import java.time.format.DateTimeFormatter


object Repository {

     val dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
    var healthConnectClient: HealthConnectClient? = null

    val stepsData: StepsData by lazy { StepsData(healthConnectClient!!) }
    val distanceData: DistanceData by lazy { DistanceData(healthConnectClient!!) }
    val exerciseMinutesData: ExerciseMinutesData by lazy { ExerciseMinutesData(healthConnectClient!!) }
    val sleepData: SleepData by lazy { SleepData(healthConnectClient!!) }
    val caloriesData: CaloriesData by lazy { CaloriesData(healthConnectClient!!) }

    val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getWritePermission(SleepSessionRecord::class),
        HealthPermission.getWritePermission(DistanceRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class)
    )

    fun checkForHealthConnectInstalled(context: Context): Int {
        val availabilityStatus =
            HealthConnectClient.getSdkStatus(context, "com.google.android.apps.healthdata")
        when (availabilityStatus) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
            /* Handle unavailable state */
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
            /* Handle provider update required state */
            }
            HealthConnectClient.SDK_AVAILABLE -> {
                healthConnectClient = HealthConnectClient.getOrCreate(context)
            }
        }
        return availabilityStatus
    }

    suspend fun checkPermissions(): Boolean {
        val granted = healthConnectClient?.permissionController?.getGrantedPermissions()
        return granted?.containsAll(PERMISSIONS) ?: false
    }

}