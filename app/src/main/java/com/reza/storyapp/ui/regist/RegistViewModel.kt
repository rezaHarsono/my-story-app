package com.reza.storyapp.ui.regist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reza.storyapp.data.Result
import com.reza.storyapp.data.UserRepository
import com.reza.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch

class RegistViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> get() = _registerResult

    fun register(
        name: String,
        email: String,
        password: String
    ) {
        _registerResult.value = Result.Loading
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)
                _registerResult.value = response
            } catch (e: Exception) {
                Log.e("RegistViewModel", "Register failed", e)
                _registerResult.value = Result.Error(e.message.toString())
            }
        }
    }
}