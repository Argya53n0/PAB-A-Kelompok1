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
import org.json.JSONObject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class RegisterSuccess(val email: String) : AuthState()
    object OtpSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Helper function to extract a human-readable error message from the API error response body.
     * Laravel validation errors return JSON like: {"message": "...", "errors": {"field": ["msg"]}}
     */
    private fun parseErrorBody(errorBody: okhttp3.ResponseBody?, fallback: String): String {
        return try {
            val bodyString = errorBody?.string() ?: return fallback
            val json = JSONObject(bodyString)
            // Try to get the main "message" field first
            val message = json.optString("message", "")
            // If there are detailed validation errors, append the first one
            val errors = json.optJSONObject("errors")
            if (errors != null && errors.keys().hasNext()) {
                val firstKey = errors.keys().next()
                val firstError = errors.optJSONArray(firstKey)?.optString(0, "")
                if (!firstError.isNullOrEmpty()) return firstError
            }
            if (message.isNotEmpty()) message else fallback
        } catch (e: Exception) {
            fallback
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = mapOf("email" to email, "password" to pass)
                val response = apiService.login(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    authResponse.token?.let { sessionManager.saveAuthToken(it) }
                    
                    if (authResponse.user != null) {
                        _authState.value = AuthState.Success(authResponse.user)
                    } else {
                        _authState.value = AuthState.Error("User data is missing")
                    }
                } else {
                    val msg = parseErrorBody(response.errorBody(), "Login failed: ${response.message()}")
                    _authState.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    fun register(name: String, email: String, phone: String, pass: String, passConf: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = mutableMapOf(
                    "name" to name,
                    "email" to email,
                    "password" to pass,
                    "password_confirmation" to passConf
                )
                if (phone.isNotEmpty()) {
                    request["phone"] = phone
                }

                val response = apiService.register(request)
                
                if (response.isSuccessful) {
                    // Registration successful, proceed to OTP verification.
                    _authState.value = AuthState.RegisterSuccess(email)
                } else {
                    val msg = parseErrorBody(response.errorBody(), "Registration failed: ${response.message()}")
                    _authState.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = mapOf("email" to email, "otp_code" to otp)
                val response = apiService.verifyOtp(request)

                if (response.isSuccessful) {
                    _authState.value = AuthState.OtpSuccess
                } else {
                    val msg = parseErrorBody(response.errorBody(), "Verifikasi OTP gagal: ${response.message()}")
                    _authState.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
