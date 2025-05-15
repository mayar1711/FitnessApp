package com.example.fitnessapp.model.datasource.local

import com.example.fitnessapp.model.datasource.model.VitalsData

interface HealthLocalDataSource {
    suspend fun getVitalsData(): VitalsData

}