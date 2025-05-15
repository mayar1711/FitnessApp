package com.example.fitnessapp.model.repository

import com.example.fitnessapp.model.datasource.model.VitalsData

interface HealthRepository {

    suspend fun getVitalsData(): VitalsData

    }
