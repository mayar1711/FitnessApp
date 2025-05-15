package com.example.fitnessapp.utils

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*


val requiredHealthPermission = setOf(
   HealthPermission.getReadPermission(StepsRecord::class),
   HealthPermission.getReadPermission(SleepSessionRecord::class),
   HealthPermission.getReadPermission(DistanceRecord::class),
   HealthPermission.getReadPermission(ExerciseSessionRecord::class),
   HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
   HealthPermission.getReadPermission(BloodGlucoseRecord::class),
   HealthPermission.getReadPermission(HeartRateRecord::class),
   HealthPermission.getReadPermission(OxygenSaturationRecord::class),
   HealthPermission.getReadPermission(HeightRecord::class),
   HealthPermission.getReadPermission(WeightRecord::class),
   HealthPermission.getReadPermission(RespiratoryRateRecord::class),
   HealthPermission.getReadPermission(BodyTemperatureRecord::class),
   HealthPermission.getReadPermission(BloodPressureRecord::class ),
   HealthPermission.getWritePermission(StepsRecord::class),
   HealthPermission.getWritePermission(SleepSessionRecord::class),
   HealthPermission.getWritePermission(DistanceRecord::class),
   HealthPermission.getWritePermission(ExerciseSessionRecord::class),
   HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
   HealthPermission.getWritePermission(BloodGlucoseRecord::class),
   HealthPermission.getWritePermission(HeartRateRecord::class),
   HealthPermission.getWritePermission(OxygenSaturationRecord::class),
   HealthPermission.getWritePermission(HeightRecord::class),
   HealthPermission.getWritePermission(WeightRecord::class),
   HealthPermission.getWritePermission(RespiratoryRateRecord::class),
   HealthPermission.getWritePermission(BloodPressureRecord::class),

   )