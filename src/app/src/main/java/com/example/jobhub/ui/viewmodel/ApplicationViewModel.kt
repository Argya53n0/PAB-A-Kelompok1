package com.example.jobhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobhub.data.local.SessionManager
import com.example.jobhub.data.model.Application
import com.example.jobhub.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed class ApplicationState {
    object Loading : ApplicationState()
    data class Success(val applications: List<Application>) : ApplicationState()
    data class Error(val message: String) : ApplicationState()
}

sealed class CancelApplicationState {
    object Idle : CancelApplicationState()
    object Loading : CancelApplicationState()
    data class Success(val message: String) : CancelApplicationState()
    data class Error(val message: String) : CancelApplicationState()
}

class ApplicationViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _applicationState = MutableStateFlow<ApplicationState>(ApplicationState.Loading)
    val applicationState: StateFlow<ApplicationState> = _applicationState.asStateFlow()

    private val _cancelState = MutableStateFlow<CancelApplicationState>(CancelApplicationState.Idle)
    val cancelState: StateFlow<CancelApplicationState> = _cancelState.asStateFlow()

    fun fetchApplications() {
        viewModelScope.launch {
            _applicationState.value = ApplicationState.Loading
            try {
                val response = apiService.getApplications()
                if (response.isSuccessful && response.body() != null) {
                    _applicationState.value = ApplicationState.Success(response.body()!!)
                } else {
                    val errorMsg = parseErrorBody(response.errorBody(), "Gagal mengambil daftar lamaran: ${response.message()}")
                    _applicationState.value = ApplicationState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _applicationState.value = ApplicationState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun cancelApplication(applicationId: Int) {
        viewModelScope.launch {
            _cancelState.value = CancelApplicationState.Loading
            try {
                val response = apiService.deleteApplication(applicationId)
                if (response.isSuccessful) {
                    val message = response.body()?.get("message") ?: "Lamaran berhasil dibatalkan"
                    _cancelState.value = CancelApplicationState.Success(message)
                    // Refresh data
                    fetchApplications()
                } else {
                    val errorMsg = parseErrorBody(response.errorBody(), "Gagal membatalkan lamaran: ${response.message()}")
                    _cancelState.value = CancelApplicationState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _cancelState.value = CancelApplicationState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun resetCancelState() {
        _cancelState.value = CancelApplicationState.Idle
    }

    private fun parseErrorBody(errorBody: okhttp3.ResponseBody?, fallback: String): String {
        return try {
            val bodyString = errorBody?.string() ?: return fallback
            val json = JSONObject(bodyString)
            json.optString("message", fallback)
        } catch (e: Exception) {
            fallback
        }
    }
}
