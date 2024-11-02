package com.reza.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.reza.storyapp.data.Result
import com.reza.storyapp.data.UserRepository
import com.reza.storyapp.data.remote.pref.User
import com.reza.storyapp.data.remote.response.LoginResponse

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    suspend fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        return userRepository.login(email, password)
    }

    suspend fun saveSession(user: User) {
        userRepository.saveSession(user)
    }
}