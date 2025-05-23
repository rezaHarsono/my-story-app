package com.reza.storyapp.di

import android.content.Context
import com.reza.storyapp.data.UserRepository
import com.reza.storyapp.data.local.StoryRoomDatabase
import com.reza.storyapp.data.local.pref.UserPreference
import com.reza.storyapp.data.local.pref.dataStore
import com.reza.storyapp.data.remote.retrofit.ApiConfig
import com.reza.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val database = StoryRoomDatabase.getInstance(context)

        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(database ,apiService, pref)
    }

    fun provideApiService(context: Context): ApiService {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        return ApiConfig.getApiService(user.token)
    }
}