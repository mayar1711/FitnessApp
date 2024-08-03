package com.example.fitnessapp.model.datasource

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.fitnessapp.model.DataRecord
import com.example.fitnessapp.model.DataType
import com.example.fitnessapp.model.Repository
import com.example.fitnessapp.model.Repository.dateTimeFormatter
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class SleepData(private val healthConnectClient: HealthConnectClient) : HealthDataReader,
    HealthDataWriter {

    override suspend fun readDataForInterval(interval: Long): List<DataRecord> {
        val startTime: ZonedDateTime =
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(interval-1)

        val sleepData = mutableListOf<DataRecord>()
        val response =
            Repository.healthConnectClient?.readRecords(
                ReadRecordsRequest(
                    SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime.toLocalDateTime(),
                        LocalDateTime.now(ZoneId.systemDefault())
                    )
                )
            )
        if (response != null) {
            if (response.records.isEmpty()) {
                sleepData.add(
                    DataRecord(
                        metricValue = "0",
                        dataType = DataType.SLEEP,
                        toDatetime = startTime.plusSeconds(1).format(dateTimeFormatter),
                        fromDatetime = startTime.format(dateTimeFormatter)
                    )
                )
            } else {
                var start = response.records[0].startTime
                var end = response.records[0].endTime

                for (index in 1 until response.records.size) {
                    if (response.records[index].startTime > end) {
                        sleepData.add(
                            DataRecord(
                                metricValue = Duration.between(start, end).toMinutes()
                                    .toString(),
                                dataType = DataType.SLEEP,
                                toDatetime = end.atZone(ZoneId.systemDefault())
                                    .format(dateTimeFormatter),
                                fromDatetime = start.atZone(ZoneId.systemDefault())
                                    .format(dateTimeFormatter)
                            )
                        )
                        start = response.records[index].startTime
                        end = response.records[index].endTime
                    } else if (response.records[index].endTime >= end) {
                        end = response.records[index].endTime
                    }
                }
                sleepData.add(
                    DataRecord(
                        metricValue = Duration.between(start, end).toMinutes().toString(),
                        dataType = DataType.SLEEP,
                        toDatetime = end.atZone(ZoneId.systemDefault())
                            .format(dateTimeFormatter),
                        fromDatetime = start.atZone(ZoneId.systemDefault())
                            .format(dateTimeFormatter)
                    )
                )

            }
        }

        Log.d("data", sleepData.toString())
        return sleepData
    }

    override suspend fun writeData(data: Any, startTime: ZonedDateTime, endTime: ZonedDateTime) {
        val duration = data as Long
        val sleepSessionRecord = SleepSessionRecord(
            startTime = startTime.toInstant(),
            startZoneOffset = startTime.offset,
            endTime = endTime.toInstant(),
            endZoneOffset = endTime.offset
        )
        healthConnectClient.insertRecords(listOf(sleepSessionRecord))
    }

}
