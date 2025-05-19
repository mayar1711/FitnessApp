package com.example.fitnessapp.ui.home.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fitnessapp.model.datasource.model.VitalsData
import com.example.fitnessapp.ui.home.viewmodel.HealthConnectViewModel
import com.example.fitnessapp.ui.state.UiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HealthConnectScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<HealthConnectViewModel>(relaxed = true)
    private val vitalsFlow = MutableStateFlow<UiState<VitalsData>>(UiState.Loading)

    @Before
     fun setupMocks() {
        every { mockViewModel.uiState } returns vitalsFlow
        coEvery { mockViewModel.fetchHealthData() } returns Unit

    }
    @Test
    fun whenLoading_showsCircularProgressIndicator() {
        vitalsFlow.value = UiState.Loading

        composeTestRule.setContent {
            androidx.compose.material3.MaterialTheme {
                HealthConnectScreen(
                    viewModel = mockViewModel,
                    showPermissionsFlow = false
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog("Test_UI")
        composeTestRule.onNodeWithTag("LoadingIndicator").assertExists()
    }

    @Test
    fun whenError_showsErrorMessage() {
        val errorMsg = "Error loading data"
        vitalsFlow.value = UiState.Error(errorMsg)

        composeTestRule.setContent {
            androidx.compose.material3.MaterialTheme {
                HealthConnectScreen(
                    viewModel = mockViewModel,
                    showPermissionsFlow = false
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog("Test_UI")
        composeTestRule.onNodeWithText(errorMsg).assertIsDisplayed()
    }

    @Test
    fun whenSuccess_showsHealthyData() {
        val dummyData = VitalsData( steps = "1000",
            calories = "500",
            sleep = "7",
            distance = "2",
            bloodSugar = "5.5",
            oxygenSaturation = "98",
            heartRate = "72",
            weight = "75.0",
            height = "180",
            temperature = "36.5",
            bloodPressure = "120/80",
            respiratoryRate = "16"
        )
        vitalsFlow.value = UiState.Success(dummyData)

        composeTestRule.setContent {
            androidx.compose.material3.MaterialTheme {
                HealthConnectScreen(
                    viewModel = mockViewModel,
                    showPermissionsFlow = false
                )
            }
        }
        composeTestRule.onNodeWithTag("Display Healthy Data").assertIsDisplayed()
    }
}
