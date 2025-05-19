package com.example.fitnessapp.ui.home.viewmodel

import com.example.fitnessapp.model.datasource.model.VitalsData
import com.example.fitnessapp.model.repository.RepositoryImpl
import com.example.fitnessapp.ui.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class HealthConnectViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: RepositoryImpl
    private lateinit var viewModel: HealthConnectViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(RepositoryImpl::class.java)
        viewModel = HealthConnectViewModel(repository)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState is Idle`() = runTest {
        assertEquals(UiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `fetchHealthData updates uiState to Success on repository success`() = runTest {
        val mockVitals = VitalsData(
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
        `when`(repository.getVitalsData()).thenReturn(mockVitals)

        viewModel.fetchHealthData()
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(mockVitals, (state as UiState.Success).data)
    }

    @Test
    fun `fetchHealthData updates uiState to Error on repository exception`() = runTest {
        val errorMessage = "Failed to fetch data"
        `when`(repository.getVitalsData()).thenThrow(RuntimeException(errorMessage))

        viewModel.fetchHealthData()
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals(errorMessage, (state as UiState.Error).message)
    }

}
