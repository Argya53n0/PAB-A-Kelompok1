package com.example.jobhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobhub.data.local.SessionManager
import com.example.jobhub.data.model.DashboardResponse
import com.example.jobhub.data.model.User
import com.example.jobhub.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: DashboardResponse, val user: User?) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private var hasFetched = false

    fun fetchDashboardIfNeeded() {
        if (!hasFetched) {
            hasFetched = true
            fetchDashboard()
        }
    }

    fun fetchDashboard() {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading
            try {
                val dashboardResponse = apiService.getDashboard()
                val userResponse = apiService.getMe() // Ambil data user login

                if (dashboardResponse.isSuccessful && dashboardResponse.body() != null) {
                    _dashboardState.value = DashboardState.Success(
                        data = dashboardResponse.body()!!,
                        user = userResponse.body()
                    )
                } else {
                    _dashboardState.value = DashboardState.Error("Gagal memuat dashboard")
                }
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }
}
