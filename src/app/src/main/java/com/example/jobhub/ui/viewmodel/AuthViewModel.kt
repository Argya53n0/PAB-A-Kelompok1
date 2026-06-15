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

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

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
                    _authState.value = AuthState.Error("Login failed: ${response.message()}")
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
                
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    authResponse.token?.let { sessionManager.saveAuthToken(it) }
                    
                    if (authResponse.user != null) {
                        _authState.value = AuthState.Success(authResponse.user)
                    } else {
                        _authState.value = AuthState.Error("User data is missing")
                    }
                } else {
                    _authState.value = AuthState.Error("Registration failed: ${response.message()}")
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
