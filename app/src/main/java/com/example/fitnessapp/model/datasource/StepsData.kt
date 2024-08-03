package com.example.fitnessapp.model.datasource

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.fitnessapp.model.DataRecord
import com.example.fitnessapp.model.DataType
import com.example.fitnessapp.model.Repository.dateTimeFormatter
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.TimeZone

class StepsData(private val healthConnectClient: HealthConnectClient) : HealthDataReader,
    HealthDataWriter {

    override suspend fun readDataForInterval(interval: Long): List<DataRecord> {
        val startTime: ZonedDateTime =
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(interval-1)

        val endTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).minusMinutes(1)
            .plusSeconds(59)
        Log.i("TAG", "readDataForInterval: $startTime")
        Log.i("TAG", "readDataForInterval: $endTime")
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime.toLocalDate().atStartOfDay(),
                        endTime.toLocalDateTime()
                    ),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )
        if (response != null) {
            val stepsData = mutableListOf<DataRecord>()
            response.sortedBy { it.startTime }
            var trackTime = startTime.toLocalDate().atStartOfDay()
            for (dailyResult in response) {
                if (dailyResult.startTime.isAfter(trackTime)) {
                    while (trackTime.isBefore(dailyResult.startTime)) {
                        stepsData.add(
                            DataRecord(
                                metricValue = "0",
                                dataType = DataType.STEPS,
                                toDatetime = trackTime.toLocalDate().atTime(LocalTime.MAX)
                                    .atZone(ZoneId.systemDefault()).format(
                                        dateTimeFormatter
                                    ),
                                fromDatetime = if (trackTime.toLocalDate()
                                        .isEqual(startTime.toLocalDate())
                                ) startTime.format(dateTimeFormatter) else trackTime.atZone(ZoneId.systemDefault())
                                    .format(dateTimeFormatter)
                            )
                        )
                        trackTime = trackTime.plusDays(1)
                    }
                }
                val totalSteps = dailyResult.result[StepsRecord.COUNT_TOTAL]
                stepsData.add(
                    DataRecord(
                        metricValue = (totalSteps ?: 0).toString(),
                        dataType = DataType.STEPS,
                        toDatetime = dailyResult.endTime.atZone(ZoneId.systemDefault())
                            .minusSeconds(1)
                            .format(dateTimeFormatter),
                        fromDatetime = if (dailyResult.startTime.toLocalDate()
                                .isEqual(startTime.toLocalDate())
                        ) startTime.format(
                            dateTimeFormatter
                        ) else dailyResult.startTime.atZone(ZoneId.systemDefault())
                            .format(dateTimeFormatter)
                    )
                )
                trackTime = dailyResult.endTime
            }
            while (trackTime.isBefore(endTime.toLocalDateTime()) && Duration.between(trackTime,endTime).toMinutes()>1) {
                stepsData.add(
                    DataRecord(
                        metricValue = "0",
                        dataType = DataType.STEPS,
                        toDatetime = if (trackTime.toLocalDate().isEqual(endTime.toLocalDate()))
                            endTime.format(dateTimeFormatter)
                        else trackTime.toLocalDate().atTime(LocalTime.MAX)
                            .atZone(ZoneId.systemDefault())
                            .format(dateTimeFormatter),
                        fromDatetime = if (trackTime.toLocalDate()
                                .isEqual(startTime.toLocalDate())
                        )
                            startTime.format(dateTimeFormatter)
                        else trackTime.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)
                    )
                )
                trackTime = trackTime.plusDays(1).toLocalDate().atStartOfDay()
            }
            Log.d("Data", stepsData.toString())
            return stepsData
        }
        return emptyList()
    }

    override suspend fun writeData(data: Any, startTime: ZonedDateTime, endTime: ZonedDateTime) {
        val steps = data as Long
        val stepsRecord = StepsRecord(
            count = steps,
            startTime = startTime.toInstant(),
            endTime = endTime.toInstant(),
            startZoneOffset = startTime.offset,
            endZoneOffset = endTime.offset
        )
        healthConnectClient.insertRecords(listOf(stepsRecord))
    }
}
