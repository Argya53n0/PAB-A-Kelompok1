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
import org.json.JSONObject

sealed class JobDetailState {
    object Loading : JobDetailState()
    data class Success(val job: JobListing) : JobDetailState()
    data class Error(val message: String) : JobDetailState()
}

sealed class ApplyJobState {
    object Idle : ApplyJobState()
    object Loading : ApplyJobState()
    data class Success(val message: String) : ApplyJobState()
    data class Error(val message: String) : ApplyJobState()
}

class JobDetailViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _jobDetailState = MutableStateFlow<JobDetailState>(JobDetailState.Loading)
    val jobDetailState: StateFlow<JobDetailState> = _jobDetailState.asStateFlow()

    private val _applyJobState = MutableStateFlow<ApplyJobState>(ApplyJobState.Idle)
    val applyJobState: StateFlow<ApplyJobState> = _applyJobState.asStateFlow()

    fun fetchJobDetail(jobId: Int) {
        viewModelScope.launch {
            _jobDetailState.value = JobDetailState.Loading
            try {
                val response = apiService.getJobDetail(jobId)
                if (response.isSuccessful && response.body() != null) {
                    _jobDetailState.value = JobDetailState.Success(response.body()!!)
                } else {
                    val errorMsg = parseErrorBody(response.errorBody(), "Gagal mengambil detail pekerjaan: ${response.message()}")
                    _jobDetailState.value = JobDetailState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _jobDetailState.value = JobDetailState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun applyJob(jobId: Int, coverLetter: String) {
        viewModelScope.launch {
            _applyJobState.value = ApplyJobState.Loading
            try {
                val request = mapOf("cover_letter" to coverLetter)
                val response = apiService.applyJob(jobId, request)
                if (response.isSuccessful && response.body() != null) {
                    _applyJobState.value = ApplyJobState.Success(response.body()!!.message)
                } else {
                    val errorMsg = parseErrorBody(response.errorBody(), "Gagal melamar pekerjaan: ${response.message()}")
                    _applyJobState.value = ApplyJobState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _applyJobState.value = ApplyJobState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun resetApplyState() {
        _applyJobState.value = ApplyJobState.Idle
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
