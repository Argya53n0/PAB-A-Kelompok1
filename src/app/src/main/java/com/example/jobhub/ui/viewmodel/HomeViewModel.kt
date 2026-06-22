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

    private var allJobs: List<JobListing> = emptyList()
    private var hasFetched = false

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(listOf("Semua"))
    val categories = _categories.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterJobs()
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        filterJobs()
    }

    private fun filterJobs() {
        val query = _searchQuery.value.lowercase()
        val category = _selectedCategory.value

        var filtered = allJobs

        if (category != "Semua") {
            filtered = filtered.filter { it.category?.name == category }
        }

        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.title.lowercase().contains(query) ||
                (it.company?.name?.lowercase()?.contains(query) == true) ||
                it.location.lowercase().contains(query)
            }
        }

        _homeState.value = HomeState.Success(filtered)
    }

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
                    allJobs = jobResponse.data
                    
                    // Extract unique categories
                    val uniqueCategories = allJobs.mapNotNull { it.category?.name }.distinct()
                    _categories.value = listOf("Semua") + uniqueCategories
                    
                    filterJobs()
                } else {
                    _homeState.value = HomeState.Error("Failed to load jobs: ${response.message()}")
                }
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }
}
