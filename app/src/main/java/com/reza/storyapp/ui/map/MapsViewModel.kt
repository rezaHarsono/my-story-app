package com.reza.storyapp.ui.map

import androidx.lifecycle.ViewModel
import com.reza.storyapp.data.UserRepository

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getStoriesWithLocation() = userRepository.getStoriesWithLocation()
}