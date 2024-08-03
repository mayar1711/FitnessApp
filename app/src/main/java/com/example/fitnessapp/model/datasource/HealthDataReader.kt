package com.example.fitnessapp.model.datasource

import com.example.fitnessapp.model.DataRecord
import java.time.ZonedDateTime

interface HealthDataReader {
    suspend fun readDataForInterval(interval: Long): List<DataRecord>
}

interface HealthDataWriter {
    suspend fun writeData(data: Any, startTime: ZonedDateTime, endTime: ZonedDateTime)
}
