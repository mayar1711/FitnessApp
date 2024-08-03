package com.example.fitnessapp.model.datasource


import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
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

class CaloriesData(private val healthConnectClient: HealthConnectClient) : HealthDataReader,
    HealthDataWriter {

    val dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

    override suspend fun readDataForInterval(interval: Long): List<DataRecord> {
        val startTime: ZonedDateTime =
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(interval - 1)

        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime.toLocalDateTime(),
                        endTime.toLocalDateTime()
                    ),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

        val caloriesData = mutableListOf<DataRecord>()

        response?.let {
            it.sortedBy { record -> record.startTime }
            var trackTime = startTime.toLocalDate().atStartOfDay()
            for (dailyResult in it) {
                if (dailyResult.startTime.isAfter(trackTime)) {
                    while (trackTime.isBefore(dailyResult.startTime)) {
                        caloriesData.add(
                            DataRecord(
                                metricValue = "0",
                                dataType = DataType.CALORIES,
                                toDatetime = trackTime.toLocalDate().atTime(LocalTime.MAX)
                                    .atZone(ZoneId.systemDefault()).format(dateTimeFormatter),
                                fromDatetime = if (trackTime.toLocalDate()
                                        .isEqual(startTime.toLocalDate())
                                ) startTime.format(dateTimeFormatter) else trackTime.atZone(ZoneId.systemDefault())
                                    .format(dateTimeFormatter)
                            )
                        )
                        trackTime = trackTime.plusDays(1).toLocalDate().atStartOfDay()
                    }
                }
                val totalCalories = dailyResult.result[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inCalories
                caloriesData.add(
                    DataRecord(
                        metricValue = (totalCalories ?: 0.0).toString(),
                        dataType = DataType.CALORIES,
                        toDatetime = dailyResult.endTime.atZone(ZoneId.systemDefault())
                            .minusSeconds(1).format(dateTimeFormatter),
                        fromDatetime = if (dailyResult.startTime.toLocalDate()
                                .isEqual(startTime.toLocalDate())
                        ) startTime.format(dateTimeFormatter) else dailyResult.startTime.atZone(ZoneId.systemDefault())
                            .format(dateTimeFormatter)
                    )
                )
                trackTime = dailyResult.endTime
            }
            while (trackTime.isBefore(endTime.toLocalDateTime()) && Duration.between(trackTime, endTime).toMinutes() > 1) {
                caloriesData.add(
                    DataRecord(
                        metricValue = "0",
                        dataType = DataType.CALORIES,
                        toDatetime = if (trackTime.toLocalDate()
                                .isEqual(endTime.toLocalDate())
                        )
                            endTime.format(dateTimeFormatter)
                        else trackTime.toLocalDate()
                            .atTime(LocalTime.MAX).atZone(ZoneId.systemDefault())
                            .format(dateTimeFormatter),
                        fromDatetime = if (trackTime.toLocalDate()
                                .isEqual(startTime.toLocalDate())
                        )
                            startTime.format(dateTimeFormatter)
                        else trackTime.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)
                    )
                )
                trackTime = trackTime.plusDays(1)
            }
        }

        return caloriesData
    }

    override suspend fun writeData(data: Any, startTime: ZonedDateTime, endTime: ZonedDateTime) {
        val calories = data as Double
        val energy = Energy.calories(calories)
        val caloriesRecord = TotalCaloriesBurnedRecord(
            energy = energy,
            startTime = startTime.toInstant(),
            startZoneOffset = startTime.offset,
            endTime = endTime.toInstant(),
            endZoneOffset = endTime.offset
        )
        healthConnectClient.insertRecords(listOf(caloriesRecord))
    }
}
