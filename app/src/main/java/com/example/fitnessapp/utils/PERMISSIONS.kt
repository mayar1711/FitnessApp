package com.example.fitnessapp.utils

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord

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
