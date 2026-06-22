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

sealed class BookmarksState {
    object Loading : BookmarksState()
    data class Success(val bookmarks: List<Bookmark>) : BookmarksState()
    data class Error(val message: String) : BookmarksState()
}

class BookmarksViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val apiService = ApiClient.getApiService(sessionManager)

    private val _bookmarksState = MutableStateFlow<BookmarksState>(BookmarksState.Loading)
    val bookmarksState: StateFlow<BookmarksState> = _bookmarksState.asStateFlow()

    fun fetchBookmarks() {
        viewModelScope.launch {
            _bookmarksState.value = BookmarksState.Loading
            try {
                val response = apiService.getBookmarks()
                if (response.isSuccessful && response.body() != null) {
                    _bookmarksState.value = BookmarksState.Success(response.body()!!)
                } else {
                    _bookmarksState.value = BookmarksState.Error("Gagal memuat lowongan tersimpan")
                }
            } catch (e: Exception) {
                _bookmarksState.value = BookmarksState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }
}
