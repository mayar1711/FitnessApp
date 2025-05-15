package com.example.fitnessapp.model.datasource.local


import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.fitnessapp.model.datasource.model.VitalsData
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class HealthLocalDataSourceImp @Inject constructor(
    private val healthConnectClient: HealthConnectClient?
) : HealthLocalDataSource {

    private val startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
    private val endTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).minusMinutes(1).plusSeconds(59)

    private suspend inline fun <reified T : Record> readMetric(
        crossinline metricExtractor: (T) -> Double?
    ): Double? {
        val response = healthConnectClient?.readRecords(
            ReadRecordsRequest(
                T::class,
                timeRangeFilter = TimeRangeFilter.between(
                    startTime.toLocalDate().atStartOfDay(),
                    endTime.toLocalDateTime()
                )
            )
        )
        return response?.records?.takeIf { it.isNotEmpty() }
            ?.map { metricExtractor(it as T) }
            ?.lastOrNull()
    }

    override suspend fun getVitalsData(): VitalsData {
        return VitalsData(
            steps = String.format(Locale.getDefault(), "%.0f", readMetric<StepsRecord> { it.count.toDouble() }),
            calories = String.format(Locale.getDefault(), "%.0f", readMetric<TotalCaloriesBurnedRecord> { it.energy.inKilocalories }),
            sleep = String.format(Locale.getDefault(), "%.0f", readMetric<SleepSessionRecord> {
                val start = it.startTime
                val end = it.endTime
                Duration.between(start, end).toHours().toDouble() }),
            distance = String.format(Locale.getDefault(), "%.0f", readMetric<DistanceRecord> { it.distance.inMeters }),
            bloodSugar = String.format(Locale.getDefault(), "%.0f", readMetric<BloodGlucoseRecord> { it.level.inMillimolesPerLiter }),
            oxygenSaturation = String.format(Locale.getDefault(), "%.0f", readMetric<OxygenSaturationRecord> { it.percentage.value }),
            heartRate = String.format(Locale.getDefault(), "%.0f", readMetric<HeartRateRecord> { it.samples.map { sample -> sample.beatsPerMinute }.average() }),
            weight = readMetric<WeightRecord> { if (it.weight.inKilograms == 0.0) null else it.weight.inKilograms }?.let { String.format(Locale.getDefault(), "%.1f", it) } ?: "",
            height = String.format(Locale.getDefault(), "%.0f", readMetric<HeightRecord> { it.height.inMeters }),
            temperature = readMetric<BodyTemperatureRecord> { it.temperature.inCelsius }?.let { String.format(Locale.getDefault(), "%.1f", it) } ?: "",
            bloodPressure = getAverageBloodPressure(),
            respiratoryRate = readMetric<RespiratoryRateRecord> { it.rate }?.let { String.format(Locale.getDefault(), "%.1f", it) } ?: ""
        )
    }
    private suspend fun getAverageBloodPressure(): String {
        val response = healthConnectClient?.readRecords(
            ReadRecordsRequest(
                BloodPressureRecord::class,
                timeRangeFilter = TimeRangeFilter.between(
                    startTime.toLocalDate().atStartOfDay(),
                    endTime.toLocalDateTime()
                )
            )
        )
        val averageSystolic = response?.records?.map { it.systolic.inMillimetersOfMercury }?.lastOrNull()?.toInt()
        val averageDiastolic = response?.records?.map { it.diastolic.inMillimetersOfMercury }?.lastOrNull()?.toInt()
        return if (averageSystolic == null || averageDiastolic == null || averageSystolic == 0 || averageDiastolic == 0) {
            ""
        } else {
            "${averageSystolic}/${averageDiastolic}"
        }
    }

}