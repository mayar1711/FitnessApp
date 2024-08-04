package com.example.fitnessapp.model.repository

import android.content.Context

interface HealthRepository {
    fun checkForHealthConnectInstalled(context: Context): Int
    suspend fun checkPermissions(): Boolean
}
