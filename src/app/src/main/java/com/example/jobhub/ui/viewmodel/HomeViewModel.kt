package com.example.jobhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobhub.data.local.SessionManager
import com.example.jobhub.data.model.JobListing
import com.example.jobhub.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val jobs: List<JobListing>) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    private var hasFetched = false

    fun fetchJobsIfNeeded() {
        if (!hasFetched) {
            hasFetched = true
            fetchJobs()
        }
    }

    fun fetchJobs() {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading
            try {
                val response = apiService.getJobs()
                if (response.isSuccessful && response.body() != null) {
                    val jobResponse = response.body()!!
                    _homeState.value = HomeState.Success(jobResponse.data)
                } else {
                    _homeState.value = HomeState.Error("Failed to load jobs: ${response.message()}")
                }
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }
}
