package com.example.fitnessapp.model.datasource

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.fitnessapp.model.DataRecord
import com.example.fitnessapp.model.DataType
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import android.util.Log

class ExerciseMinutesData(private val healthConnectClient: HealthConnectClient) : HealthDataReader,
    HealthDataWriter {

    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

    override suspend fun readDataForInterval(interval: Long): List<DataRecord> {
        val startTime: ZonedDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(interval - 1)
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val response = healthConnectClient.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(ExerciseSessionRecord.EXERCISE_DURATION_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(
                    startTime.toLocalDateTime(),
                    endTime.toLocalDateTime()
                ),
                timeRangeSlicer = Period.ofDays(1)
            )
        )

        if (response != null) {
            val minutesData = mutableListOf<DataRecord>()
            response.sortedBy { it.startTime }
            var trackTime = startTime.toLocalDate().atStartOfDay()
            for (dailyResult in response) {
                if (dailyResult.startTime.isAfter(trackTime)) {
                    while (trackTime.isBefore(dailyResult.startTime)) {
                        minutesData.add(
                            DataRecord(
                                metricValue = "0",
                                dataType = DataType.MINS,
                                toDatetime = trackTime.toLocalDate().atTime(LocalTime.MAX)
                                    .atZone(ZoneId.systemDefault()).format(dateTimeFormatter),
                                fromDatetime = if (trackTime.toLocalDate().isEqual(startTime.toLocalDate()))
                                    startTime.format(dateTimeFormatter)
                                else
                                    trackTime.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)
                            )
                        )
                        trackTime = trackTime.plusDays(1).toLocalDate().atStartOfDay()
                    }
                }
                val totalMins = dailyResult.result[ExerciseSessionRecord.EXERCISE_DURATION_TOTAL]?.toMinutes()
                minutesData.add(
                    DataRecord(
                        metricValue = (totalMins ?: 0).toString(),
                        dataType = DataType.MINS,
                        toDatetime = dailyResult.endTime.atZone(ZoneId.systemDefault())
                            .minusSeconds(1).format(dateTimeFormatter),
                        fromDatetime = if (dailyResult.startTime.toLocalDate().isEqual(startTime.toLocalDate()))
                            startTime.format(dateTimeFormatter)
                        else
                            dailyResult.startTime.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)
                    )
                )
                trackTime = dailyResult.endTime
            }
            while (trackTime.isBefore(endTime.toLocalDateTime()) && Duration.between(trackTime, endTime).toMinutes() > 1) {
                minutesData.add(
                    DataRecord(
                        metricValue = "0",
                        dataType = DataType.MINS,
                        toDatetime = if (trackTime.toLocalDate().isEqual(endTime.toLocalDate()))
                            endTime.format(dateTimeFormatter)
                        else
                            trackTime.toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).format(dateTimeFormatter),
                        fromDatetime = if (trackTime.toLocalDate().isEqual(startTime.toLocalDate()))
                            startTime.format(dateTimeFormatter)
                        else
                            trackTime.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)
                    )
                )
                trackTime = trackTime.plusDays(1)
            }
            Log.d("Data", minutesData.toString())
            return minutesData
        }
        return emptyList()
    }
    override suspend fun writeData(data: Any, startTime: ZonedDateTime, endTime: ZonedDateTime) {
      /*  val minutes = data as Long
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime.toInstant(),
            startZoneOffset = startTime.offset,
            endTime = endTime.toInstant(),
            endZoneOffset = endTime.offset,
            exerciseType = 0, // Replace with a valid integer value if needed
            title = "Exercise Session",
            notes = null, // Optional
            metadata = emptyMap(), // Optional
            segments = emptyList(), // Replace with actual segments if needed
            laps = emptyList(), // Replace with actual laps if needed
            exerciseRouteResult = null // Replace with actual route result if needed
        )
        healthConnectClient.insertRecords(listOf(exerciseSessionRecord))*/
    }

}
