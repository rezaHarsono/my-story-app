package com.reza.storyapp.ui.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.reza.storyapp.data.UserRepository
import com.reza.storyapp.data.local.StoryEntity
import kotlinx.coroutines.launch

class StoryViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getStories(): LiveData<PagingData<StoryEntity>> {
        _isLoading.value = true
        val result = userRepository.getStories().cachedIn(viewModelScope)
        if (result.value != null) {
            _isLoading.value = false
        } else {
            _isLoading.value = false
            Log.e("StoryViewModel", "Error retrieving stories")
        }
        return result
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}