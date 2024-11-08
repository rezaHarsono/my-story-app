package com.reza.storyapp.ui.storyDetail

import androidx.lifecycle.ViewModel
import com.reza.storyapp.data.UserRepository

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    fun getStoryById(id: String) = repository.getStoriesWithId(id)

}