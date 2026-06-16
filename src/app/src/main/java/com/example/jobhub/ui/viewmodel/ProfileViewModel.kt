package com.example.jobhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobhub.data.local.SessionManager
import com.example.jobhub.data.model.User
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

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class ProfileUpdateState {
    object Idle : ProfileUpdateState()
    object Loading : ProfileUpdateState()
    data class Success(val message: String) : ProfileUpdateState()
    data class Error(val message: String) : ProfileUpdateState()
}

class ProfileViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _updateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val updateState: StateFlow<ProfileUpdateState> = _updateState.asStateFlow()

    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = apiService.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    _profileState.value = ProfileState.Success(response.body()!!)
                } else {
                    val errorMsg = parseErrorBody(response.errorBody(), "Gagal mengambil profil: ${response.message()}")
                    _profileState.value = ProfileState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun updateProfile(
        skills: String?,
        experience: String?,
        education: String?,
        phone: String?,
        address: String?,
        resumeFile: File?
    ) {
        viewModelScope.launch {
            _updateState.value = ProfileUpdateState.Loading
            try {
                val skillsBody = skills?.toRequestBody("text/plain".toMediaTypeOrNull())
                val expBody = experience?.toRequestBody("text/plain".toMediaTypeOrNull())
                val eduBody = education?.toRequestBody("text/plain".toMediaTypeOrNull())
                val phoneBody = phone?.toRequestBody("text/plain".toMediaTypeOrNull())
                val addressBody = address?.toRequestBody("text/plain".toMediaTypeOrNull())
                
                var resumePart: MultipartBody.Part? = null
                if (resumeFile != null && resumeFile.exists()) {
                    val reqFile = resumeFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                    resumePart = MultipartBody.Part.createFormData("resume", resumeFile.name, reqFile)
                }

                val response = apiService.updateProfile(
                    skills = skillsBody,
                    experience = expBody,
                    education = eduBody,
                    phone = phoneBody,
                    address = addressBody,
                    resume = resumePart
                )

                if (response.isSuccessful && response.body() != null) {
                    _updateState.value = ProfileUpdateState.Success(response.body()!!.message)
                    // Refresh profile data
                    fetchProfile()
                } else {
                    val errorMsg = parseErrorBody(response.errorBody(), "Gagal memperbarui profil: ${response.message()}")
                    _updateState.value = ProfileUpdateState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _updateState.value = ProfileUpdateState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = ProfileUpdateState.Idle
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
