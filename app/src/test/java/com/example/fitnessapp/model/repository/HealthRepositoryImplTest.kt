package com.example.fitnessapp.model.repository

import com.example.fitnessapp.model.datasource.local.HealthLocalDataSource
import com.example.fitnessapp.model.datasource.model.VitalsData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class HealthRepositoryImplTest {

    private lateinit var localDataSource: HealthLocalDataSource
    private lateinit var repository: RepositoryImpl

    @Before
    fun setup() {
        localDataSource = mock(HealthLocalDataSource::class.java)
        repository = RepositoryImpl(localDataSource)
    }

    @Test
    fun getVitalsDataReturnsVitalsData() = runTest {
        val mockVitalsData = VitalsData(
            steps = "1000",
            calories = "500",
            sleep = "7",
            distance = "2",
            bloodSugar = "5.5",
            oxygenSaturation = "98",
            heartRate = "70",
            weight = "75.0",
            height = "180",
            temperature = "36.5",
            bloodPressure = "120/80",
            respiratoryRate = "16"
        )

        `when`(localDataSource.getVitalsData()).thenReturn(mockVitalsData)

        val result = repository.getVitalsData()

        assertEquals(mockVitalsData, result)
        verify(localDataSource, times(1)).getVitalsData()
    }

    @Test
    fun getVitalsDataReturnsNull() = runTest {
        `when`(localDataSource.getVitalsData()).thenReturn(null)

        val result = repository.getVitalsData()

        assertEquals<VitalsData?>(null, result)
        verify(localDataSource, times(1)).getVitalsData()
    }

    @Test
    fun getVitalsDataThrowsException() = runTest {
        val exception = RuntimeException("Data fetch failed")
        `when`(localDataSource.getVitalsData()).thenThrow(exception)

        val thrown = assertFailsWith<RuntimeException> {
            repository.getVitalsData()
        }
        assertEquals("Data fetch failed", thrown.message)
        verify(localDataSource, times(1)).getVitalsData()
    }

    @Test
    fun getVitalsDataHandlesEmptyFields() = runTest {
        val emptyVitalsData = VitalsData(
            steps = "",
            calories = "",
            sleep = "",
            distance = "",
            bloodSugar = "",
            oxygenSaturation = "",
            heartRate = "",
            weight = "",
            height = "",
            temperature = "",
            bloodPressure = "",
            respiratoryRate = ""
        )
        `when`(localDataSource.getVitalsData()).thenReturn(emptyVitalsData)

        val result = repository.getVitalsData()

        assertEquals(emptyVitalsData, result)
        verify(localDataSource, times(1)).getVitalsData()
    }

}
