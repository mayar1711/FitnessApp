package com.example.fitnessapp.model

class repo {
}

/*

 object HealthConnectUtils {
    private var healthConnectClient: HealthConnectClient? = null

    val stepsData: StepsData by lazy { StepsData(healthConnectClient!!) }
    val distanceData: DistanceData by lazy { DistanceData(healthConnectClient!!) }
    val exerciseMinutesData: ExerciseMinutesData by lazy { ExerciseMinutesData(healthConnectClient!!) }
    val sleepData: SleepData by lazy { SleepData(healthConnectClient!!) }
    val caloriesData: CaloriesData by lazy { CaloriesData(healthConnectClient!!) }

    val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    fun checkForHealthConnectInstalled(context: Context): Int {
        val availabilityStatus =
            HealthConnectClient.getSdkStatus(context, "com.google.android.apps.healthdata")
        when (availabilityStatus) {
            HealthConnectClient.SDK_UNAVAILABLE -> { /* Handle unavailable state */ }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> { /* Handle provider update required state */ }
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
*/