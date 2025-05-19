package com.example.fitnessapp.utils

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient

object HealthConnectClientProvider {
    fun getClient(context: Context): HealthConnectClient? {
        return try {
            HealthConnectClient.getOrCreate(context)
        } catch (e: Exception) {
            Log.e("HealthConnectClientProvider", "Failed to get HealthConnectClient", e)
            null
        }
    }

    fun getSdkStatus(context: Context): Int = HealthConnectClient.getSdkStatus(context)
}
