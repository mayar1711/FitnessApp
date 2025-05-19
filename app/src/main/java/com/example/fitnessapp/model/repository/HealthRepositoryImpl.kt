package com.example.fitnessapp.model.repository

import com.example.fitnessapp.model.datasource.local.HealthLocalDataSource
import com.example.fitnessapp.model.datasource.model.VitalsData
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val localDataSource: HealthLocalDataSource) :
    HealthRepository {

    override suspend fun getVitalsData(): VitalsData {
        return localDataSource.getVitalsData()
    }


}

