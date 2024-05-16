package com.raihanardila.bapps.core.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raihanardila.bapps.core.data.local.repository.AuthRepository
import com.raihanardila.bapps.core.data.model.AuthModel
import com.raihanardila.bapps.core.data.remote.response.auth.LoginResponse
import com.raihanardila.bapps.core.data.remote.response.auth.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    private val _authToken = MutableLiveData<String>()
    val authToken: LiveData<String> = _authToken

    fun loginUser(email: String, password: String) {
        _isLoading.value = true
        val authModel = AuthModel(email = email, password = password) // `name` is null for login
        viewModelScope.launch {
            val response: Response<LoginResponse> = authRepository.login(authModel)
            _isLoading.value = false
            if (response.isSuccessful && response.body()?.error == false) {
                _authToken.value = response.body()?.authResult?.token
                _loginSuccess.value = true
            } else {
                _loginSuccess.value = false
            }
        }
    }

    fun registerUser(name: String, email: String, password: String) {
        _isLoading.value = true
        val authModel = AuthModel(name = name, email = email, password = password)
        viewModelScope.launch {
            val response: Response<RegisterResponse> = authRepository.register(authModel)
            _isLoading.value = false
            if (response.isSuccessful && response.body()?.error == false) {
                _registerSuccess.value = true
            } else {
                _registerSuccess.value = false
            }
        }
    }
}
