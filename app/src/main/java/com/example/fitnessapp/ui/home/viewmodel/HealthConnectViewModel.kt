package com.example.fitnessapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.model.datasource.model.VitalsData
import com.example.fitnessapp.model.repository.RepositoryImpl
import com.example.fitnessapp.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HealthConnectViewModel @Inject constructor(private val repository: RepositoryImpl) :
    ViewModel() {

    private val _uiState = MutableStateFlow<UiState<VitalsData>>(UiState.Idle)
    val uiState = _uiState
    fun fetchHealthData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val data = repository.getVitalsData()
                _uiState.value = UiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

}
