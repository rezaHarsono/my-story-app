package com.reza.storyapp.ui.regist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.reza.storyapp.data.Result
import com.reza.storyapp.data.UserRepository
import com.reza.storyapp.data.remote.response.RegisterResponse

class RegistViewModel(private val userRepository: UserRepository) : ViewModel() {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> {
        return userRepository.register(name, email, password)
    }
}