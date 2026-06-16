package com.example.jobhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobhub.data.local.SessionManager
import com.example.jobhub.data.model.Bookmark
import com.example.jobhub.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed class BookmarkState {
    object Loading : BookmarkState()
    data class Success(val bookmarks: List<Bookmark>) : BookmarkState()
    data class Error(val message: String) : BookmarkState()
}

sealed class ToggleBookmarkState {
    object Idle : ToggleBookmarkState()
    object Loading : ToggleBookmarkState()
    data class Success(val message: String, val isBookmarked: Boolean) : ToggleBookmarkState()
    data class Error(val message: String) : ToggleBookmarkState()
}

class BookmarkViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _bookmarkState = MutableStateFlow<BookmarkState>(BookmarkState.Loading)
    val bookmarkState: StateFlow<BookmarkState> = _bookmarkState.asStateFlow()

    private val _toggleState = MutableStateFlow<ToggleBookmarkState>(ToggleBookmarkState.Idle)
    val toggleState: StateFlow<ToggleBookmarkState> = _toggleState.asStateFlow()

    fun fetchBookmarks() {
        viewModelScope.launch {
            _bookmarkState.value = BookmarkState.Loading
            try {
                val response = apiService.getBookmarks()
                if (response.isSuccessful && response.body() != null) {
                    _bookmarkState.value = BookmarkState.Success(response.body()!!)
                } else {
                    val errorMsg = parseErrorBody(response.errorBody(), "Gagal mengambil daftar bookmark: ${response.message()}")
                    _bookmarkState.value = BookmarkState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _bookmarkState.value = BookmarkState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun toggleBookmark(jobId: Int) {
        viewModelScope.launch {
            _toggleState.value = ToggleBookmarkState.Loading
            try {
                val response = apiService.toggleBookmark(jobId)
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    _toggleState.value = ToggleBookmarkState.Success(data.message, data.isBookmarked)
                    // Refresh data
                    fetchBookmarks()
                } else {
                    val errorMsg = parseErrorBody(response.errorBody(), "Gagal mengubah bookmark: ${response.message()}")
                    _toggleState.value = ToggleBookmarkState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _toggleState.value = ToggleBookmarkState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun resetToggleState() {
        _toggleState.value = ToggleBookmarkState.Idle
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
