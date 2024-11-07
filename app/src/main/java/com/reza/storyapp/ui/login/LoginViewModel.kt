package com.reza.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reza.storyapp.data.Result
import com.reza.storyapp.data.UserRepository
import com.reza.storyapp.data.remote.pref.User
import com.reza.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    fun login(email: String, password: String) {
        _loginResult.value = Result.Loading
        viewModelScope.launch {
            try {
                val response = userRepository.login(email, password)
                _loginResult.value = response
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login failed", e)
                _loginResult.value = Result.Error(e.message.toString())
            }
        }
    }

    fun saveSession(user: User) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }
}
