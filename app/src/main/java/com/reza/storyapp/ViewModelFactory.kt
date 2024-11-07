package com.reza.storyapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.reza.storyapp.data.UserRepository
import com.reza.storyapp.di.Injection
import com.reza.storyapp.ui.addStory.AddStoryViewModel
import com.reza.storyapp.ui.login.LoginViewModel
import com.reza.storyapp.ui.main.MainViewModel
import com.reza.storyapp.ui.map.MapsViewModel
import com.reza.storyapp.ui.regist.RegistViewModel
import com.reza.storyapp.ui.story.StoryViewModel
import com.reza.storyapp.ui.storyDetail.DetailViewModel

class ViewModelFactory private constructor(private val userRepository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistViewModel::class.java)) {
            return RegistViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class: " + modelClass.name)
    }

    companion object {
        fun getInstance(context: Context) = ViewModelFactory(Injection.provideRepository(context))
    }
}