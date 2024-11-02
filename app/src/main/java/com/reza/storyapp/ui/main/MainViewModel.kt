package com.reza.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.reza.storyapp.data.UserRepository
import com.reza.storyapp.data.remote.pref.User

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<User> {
        return userRepository.getSession().asLiveData()
    }
}