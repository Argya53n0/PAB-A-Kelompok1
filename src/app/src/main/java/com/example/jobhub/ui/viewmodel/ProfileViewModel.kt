package com.example.jobhub.ui.viewmodel

import android.content.Context
import android.net.Uri
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
import java.io.File

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User, val updateMessage: String? = null) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _isUploadingCv = MutableStateFlow(false)
    val isUploadingCv: StateFlow<Boolean> = _isUploadingCv.asStateFlow()

    private val _isUploadingPhoto = MutableStateFlow(false)
    val isUploadingPhoto: StateFlow<Boolean> = _isUploadingPhoto.asStateFlow()

    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = apiService.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    _profileState.value = ProfileState.Success(response.body()!!)
                } else {
                    _profileState.value = ProfileState.Error("Gagal memuat profil")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun updateProfile(name: String, phone: String) {
        viewModelScope.launch {
            val currentState = _profileState.value
            if (currentState is ProfileState.Success) {
                _profileState.value = ProfileState.Loading
                try {
                    val request = mapOf(
                        "name" to name,
                        "phone" to phone
                    )
                    val response = apiService.updateProfile(request)
                    if (response.isSuccessful && response.body() != null) {
                        val updatedUser = response.body()!!.user ?: currentState.user
                        _profileState.value = ProfileState.Success(
                            user = updatedUser,
                            updateMessage = "Profil berhasil diperbarui"
                        )
                        fetchProfile()
                    } else {
                        _profileState.value = ProfileState.Error("Gagal memperbarui profil")
                    }
                } catch (e: Exception) {
                    _profileState.value = ProfileState.Error(e.localizedMessage ?: "Terjadi kesalahan saat menyimpan")
                }
            }
        }
    }

    fun uploadCv(context: Context, uri: Uri) {
        uploadFile(context, uri, "resume", _isUploadingCv)
    }

    fun uploadProfilePicture(context: Context, uri: Uri) {
        uploadFile(context, uri, "profile_picture", _isUploadingPhoto)
    }

    private fun uploadFile(
        context: Context,
        uri: Uri,
        partName: String,
        loadingState: MutableStateFlow<Boolean>
    ) {
        viewModelScope.launch {
            val currentState = _profileState.value
            if (currentState is ProfileState.Success) {
                loadingState.value = true
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val fileName = getFileName(context, uri) ?: if (partName == "resume") "resume.pdf" else "photo.jpg"
                    val tempFile = File(context.cacheDir, fileName)
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    val mediaTypeStr = if (partName == "resume") "application/pdf" else "image/*"
                    val requestFile = tempFile.asRequestBody(mediaTypeStr.toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData(partName, fileName, requestFile)

                    val namePart = (currentState.user.name).toRequestBody("text/plain".toMediaTypeOrNull())
                    val phonePart = (currentState.user.jobSeeker?.phone ?: "").toRequestBody("text/plain".toMediaTypeOrNull())

                    val response = if (partName == "resume") {
                        apiService.updateProfileWithFile(namePart, phonePart, resume = filePart)
                    } else {
                        apiService.updateProfileWithFile(namePart, phonePart, profilePicture = filePart)
                    }

                    if (response.isSuccessful && response.body() != null) {
                        _profileState.value = ProfileState.Success(
                            user = response.body()!!.user ?: currentState.user,
                            updateMessage = if (partName == "resume") "CV berhasil diupload!" else "Foto profil berhasil diupload!"
                        )
                        fetchProfile()
                    } else {
                        _profileState.value = ProfileState.Success(
                            user = currentState.user,
                            updateMessage = "Gagal mengupload file"
                        )
                    }
                    tempFile.delete()
                } catch (e: Exception) {
                    _profileState.value = ProfileState.Success(
                        user = currentState.user,
                        updateMessage = "Error: ${e.localizedMessage}"
                    )
                } finally {
                    loadingState.value = false
                }
            }
        }
    }

    fun deleteCv() {
        viewModelScope.launch {
            val currentState = _profileState.value
            if (currentState is ProfileState.Success) {
                _profileState.value = ProfileState.Loading
                try {
                    val response = apiService.deleteCv()
                    if (response.isSuccessful) {
                        _profileState.value = ProfileState.Success(
                            user = response.body()?.user ?: currentState.user,
                            updateMessage = "CV berhasil dihapus"
                        )
                        fetchProfile()
                    } else {
                        _profileState.value = ProfileState.Error("Gagal menghapus CV")
                    }
                } catch (e: Exception) {
                    _profileState.value = ProfileState.Error(e.localizedMessage ?: "Terjadi kesalahan")
                }
            }
        }
    }

    fun deleteProfilePicture() {
        viewModelScope.launch {
            val currentState = _profileState.value
            if (currentState is ProfileState.Success) {
                _profileState.value = ProfileState.Loading
                try {
                    val response = apiService.deleteProfilePicture()
                    if (response.isSuccessful) {
                        _profileState.value = ProfileState.Success(
                            user = response.body()?.user ?: currentState.user,
                            updateMessage = "Foto profil berhasil dihapus"
                        )
                        fetchProfile()
                    } else {
                        _profileState.value = ProfileState.Error("Gagal menghapus foto profil")
                    }
                } catch (e: Exception) {
                    _profileState.value = ProfileState.Error(e.localizedMessage ?: "Terjadi kesalahan")
                }
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
}
