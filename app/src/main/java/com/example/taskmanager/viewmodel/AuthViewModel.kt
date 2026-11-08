package com.example.taskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.auth.AuthRepository
import com.example.taskmanager.data.remote.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val authState: StateFlow<Resource<FirebaseUser>?> = _authState.asStateFlow()

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = Resource.Error("Vui lòng nhập đầy đủ Email và Mật khẩu")
            return
        }
        _authState.value = Resource.Loading
        viewModelScope.launch {
            _authState.value = authRepository.login(email, pass)
        }
    }

    fun register(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = Resource.Error("Vui lòng nhập đầy đủ Email và Mật khẩu")
            return
        }
        if (pass.length < 6) {
            _authState.value = Resource.Error("Mật khẩu phải có ít nhất 6 ký tự")
            return
        }
        _authState.value = Resource.Loading
        viewModelScope.launch {
            _authState.value = authRepository.register(email, pass)
        }
    }

    fun resetState() {
        _authState.value = null
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
