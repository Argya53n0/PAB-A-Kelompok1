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

sealed class ApplicationsState {
    object Loading : ApplicationsState()
    data class Success(val applications: List<Application>) : ApplicationsState()
    data class Error(val message: String) : ApplicationsState()
}

class ApplicationsViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _applicationsState = MutableStateFlow<ApplicationsState>(ApplicationsState.Loading)
    val applicationsState: StateFlow<ApplicationsState> = _applicationsState.asStateFlow()

    private var allApplications: List<Application> = emptyList()

    private val _selectedFilter = MutableStateFlow("Semua")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    fun fetchApplications() {
        viewModelScope.launch {
            _applicationsState.value = ApplicationsState.Loading
            try {
                val response = apiService.getApplications()
                if (response.isSuccessful && response.body() != null) {
                    allApplications = response.body()!!
                    applyFilter()
                } else {
                    _applicationsState.value = ApplicationsState.Error("Gagal memuat daftar lamaran")
                }
            } catch (e: Exception) {
                _applicationsState.value = ApplicationsState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
        applyFilter()
    }

    private fun applyFilter() {
        val filter = _selectedFilter.value
        val filtered = if (filter == "Semua") {
            allApplications
        } else {
            allApplications.filter { 
                when(filter) {
                    "Menunggu" -> it.status.lowercase() == "waiting"
                    "Diterima" -> it.status.lowercase() == "accepted"
                    "Ditolak" -> it.status.lowercase() == "rejected"
                    else -> true
                }
            }
        }
        _applicationsState.value = ApplicationsState.Success(filtered)
    }

    fun cancelApplication(applicationId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteApplication(applicationId)
                if (response.isSuccessful) {
                    fetchApplications() // Refresh after cancel
                }
            } catch (e: Exception) {
                // Handle error optionally via toast or state
            }
        }
    }
}
