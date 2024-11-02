package com.reza.storyapp.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reza.storyapp.data.UserRepository
import kotlinx.coroutines.launch

class StoryViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getStories() = userRepository.getStories()

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}