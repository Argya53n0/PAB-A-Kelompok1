package com.example.jobhub.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobhub.data.local.SessionManager
import com.example.jobhub.data.model.JobListing
import com.example.jobhub.data.model.User
import com.example.jobhub.data.model.Bookmark
import com.example.jobhub.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

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

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

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

    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                val response = apiService.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    _userProfile.value = response.body()!!
                }
            } catch (_: Exception) {
                // Silently fail - profile info is supplementary
            }
        }
    }

    fun applyJob(jobId: Int, coverLetter: String, cvUri: Uri? = null, context: Context? = null) {
        viewModelScope.launch {
            _applyJobState.value = ApplyJobState.Loading
            try {
                val response = if (cvUri != null && context != null) {
                    // Multipart upload with CV file
                    val coverLetterPart = coverLetter.toRequestBody("text/plain".toMediaTypeOrNull())

                    val inputStream = context.contentResolver.openInputStream(cvUri)
                    val fileName = getFileName(context, cvUri) ?: "resume.pdf"
                    val tempFile = File(context.cacheDir, fileName)
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    val requestFile = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                    val cvPart = MultipartBody.Part.createFormData("cv_path", fileName, requestFile)

                    val result = apiService.applyJobWithCv(jobId, coverLetterPart, cvPart)
                    tempFile.delete()
                    result
                } else {
                    // Simple JSON request (uses profile CV)
                    val request = mapOf("cover_letter" to coverLetter)
                    apiService.applyJob(jobId, request)
                }

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

    fun checkBookmarkStatus(jobId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getBookmarks()
                if (response.isSuccessful && response.body() != null) {
                    val bookmarks = response.body()!!
                    _isBookmarked.value = bookmarks.any { it.jobListingId == jobId }
                }
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }

    fun toggleBookmark(jobId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.toggleBookmark(jobId)
                if (response.isSuccessful && response.body() != null) {
                    _isBookmarked.value = response.body()!!.isBookmarked
                }
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
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
