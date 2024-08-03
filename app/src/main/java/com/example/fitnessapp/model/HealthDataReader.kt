package com.example.fitnessapp.model

import java.time.ZonedDateTime

interface HealthDataReader {
    suspend fun readDataForInterval(interval: Long): List<DataRecord>
}

interface HealthDataWriter {
    suspend fun writeData(data: Any, startTime: ZonedDateTime, endTime: ZonedDateTime)
}
